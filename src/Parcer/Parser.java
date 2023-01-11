package Parcer;

import Expression.*;
import MathOperations.LogicImplementation;
import MathOperations.LogicInversion;
import MathOperations.LogicMultiplication;
import MathOperations.LogicSum;
import Utils.SparseTable;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private static Parser parserInstance = null;
    private Parser() {}
    public static Parser getInstance() {
        if (parserInstance == null) parserInstance = new Parser();

        return parserInstance;
    }

    private enum finiteStates {
        ANY_TYPE,
        SPACE,
        VARIABLE,
        OPERATOR,
        OPENING_BRACES,
        CLOSING_BRACES,
        FINISH
    }

    VariablesList variablesList;
    public ExpressionTree getExpressionTree(String input) {
        getExpressionSequenceFromString(input.toCharArray());
        if (expressionSequence.size() == 0) return null;
        getVariablesListFromExpressionSequence();
        sparseTable = null;
        ExpressionTreeNode root = getTreeRootFromSequence(0, expressionSequence.size() - 1);

        return new ExpressionTree(root, variablesList);
    }

    private void getVariablesListFromExpressionSequence(){
        variablesList = new VariablesList();

        for (String current : expressionSequence){
            if (Objects.equals(current, "_inv")) continue;
            if (Objects.equals(current, "_or")) continue;
            if (Objects.equals(current, "_and")) continue;
            if (Objects.equals(current, "_impl")) continue;
            if (Objects.equals(current, "_opBr")) continue;
            if (Objects.equals(current, "_clBr")) continue;

            VariableName currentName = new VariableName(current);
            boolean isNew = true;
            for (VariableName prName : variablesList.getVariableList())
                if (prName.equals(currentName)) {
                    isNew = false;
                    break;
                }

            if (isNew) variablesList.getVariableList().add(currentName);
        }
    }

    private boolean isEmpty(char ch){
        return (ch == ' ') || (ch == '\n') || (ch == '\0') || (ch == '\u0009') || (ch == '\r');
    }
    String variableSymbolRegexp = "([])";
    private boolean isCorrectVariableSymbol(char ch){
        if (('a' <= ch) && (ch <= 'z')) return true;
        if (('A' <= ch) && (ch <= 'Z')) return true;
        if (('0' <= ch) && (ch <= '9')) return true;
        if (ch == '_') return true;
        return (int)ch == 39;
    }
    String operatorSymbolRegexp = "[!|&->]";
    private boolean isCorrectOperatorSymbol(char ch){
        return (ch == '!') || (ch == '|') || (ch == '&') || (ch == '-') || (ch == '>');
    }
    private void getExpressionSequenceFromString(char[] expression){
        expressionSequence = new ArrayList<>();

        int ind = 0;

        finiteStates state = finiteStates.ANY_TYPE;
        while(state != finiteStates.FINISH)
            switch (state) {
                case ANY_TYPE:{
                    if (ind >= expression.length) {state = finiteStates.FINISH; break;}
                    if (expression[ind] == '(') {state = finiteStates.OPENING_BRACES; break;}
                    if (expression[ind] == ')') {state = finiteStates.CLOSING_BRACES; break;}
                    if (isEmpty(expression[ind])) {state = finiteStates.SPACE; break;}
                    if (isCorrectVariableSymbol(expression[ind])) {state = finiteStates.VARIABLE; break;}
                    if (isCorrectOperatorSymbol(expression[ind])) {state = finiteStates.OPERATOR; break;}
                    if (expression[ind] == '\0') state = finiteStates.FINISH;
                    break;
                }
                case SPACE: {
                    while (isEmpty(expression[ind])) {
                        ind++;
                        if (ind >= expression.length) break;
                    }
                    state = finiteStates.ANY_TYPE;
                    break;
                }
                case VARIABLE: {
                    StringBuilder name = new StringBuilder();
                    while (isCorrectVariableSymbol(expression[ind])) {
                        name.append(expression[ind]);
                        ind++;
                        if (ind >= expression.length) break;
                    }
                    expressionSequence.add(name.toString());
                    state = finiteStates.ANY_TYPE;
                    break;
                }
                case OPERATOR: {
                    if (expression[ind] == '!') {
                        expressionSequence.add("_inv");
                        ind++;
                        state = finiteStates.ANY_TYPE;
                        break;
                    }
                    if (expression[ind] == '|') {
                        expressionSequence.add("_or");
                        ind++;
                        state = finiteStates.ANY_TYPE;
                        break;
                    }
                    if (expression[ind] == '&') {
                        expressionSequence.add("_and");
                        ind++;
                        state = finiteStates.ANY_TYPE;
                        break;
                    }
                    if (expression[ind] == '-') {
                        expressionSequence.add("_impl");
                        ind += 2;
                        state = finiteStates.ANY_TYPE;
                        break;
                    }
                    break;
                }
                case OPENING_BRACES: {
                    expressionSequence.add("_opBr");
                    ind++;
                    state = finiteStates.ANY_TYPE;
                    break;
                }
                case CLOSING_BRACES: {
                    expressionSequence.add("_clBr");
                    ind++;
                    state = finiteStates.ANY_TYPE;
                    break;
                }
            }
    }

    private ArrayList<String> expressionSequence;
    private SparseTable sparseTable;

    private Integer getPriority(String current){
        if (Objects.equals(current, "_inv")) return 6;
        if (Objects.equals(current, "_and")) return 7;
        if (Objects.equals(current, "_or")) return 8;
        if (Objects.equals(current, "_impl")) return 9;
        if (Objects.equals(current, "_opBr")) return -1000*1000;
        if (Objects.equals(current, "_clBr")) return -1000*1000;

        return 1;
    }
    private void getSparseTableFromSequence(){
        ArrayList<Integer> list = new ArrayList<>();

        int current_delta = 0;
        for (String current_element : expressionSequence){
            if (Objects.equals(current_element, "_opBr")) current_delta -= 10;
            if (Objects.equals(current_element, "_clBr")) current_delta += 10;

            list.add(getPriority(current_element) + current_delta);
        }

        sparseTable = new SparseTable(list);
    }
    private ExpressionTreeNode getTreeRootFromSequence(int left, int right){
        if (sparseTable == null) getSparseTableFromSequence();
        int nodeTypeInd = sparseTable.getMaxIndexFromSequence(left, right);
        String nodeTypeName = expressionSequence.get(nodeTypeInd);

        ExpressionTreeNode node;
        switch(nodeTypeName){
            case "_inv": {
                node = new UnaryOperationNode(new LogicInversion());
                ((UnaryOperationNode)node)
                        .setBoolNode(getTreeRootFromSequence(nodeTypeInd + 1, right));
                break;
            }
            case "_or": {
                node = new BinaryOperationNode(new LogicSum());
                ((BinaryOperationNode)node)
                        .setFirstNode(getTreeRootFromSequence(left, nodeTypeInd - 1));
                ((BinaryOperationNode)node)
                        .setSecondNode(getTreeRootFromSequence(nodeTypeInd + 1, right));
                break;
            }
            case "_and": {
                node = new BinaryOperationNode(new LogicMultiplication());
                ((BinaryOperationNode)node)
                        .setFirstNode(getTreeRootFromSequence(left, nodeTypeInd - 1));
                ((BinaryOperationNode)node)
                        .setSecondNode(getTreeRootFromSequence(nodeTypeInd + 1, right));
                break;
            }
            case "_impl": {
                node = new BinaryOperationNode(new LogicImplementation());
                ((BinaryOperationNode)node)
                        .setFirstNode(getTreeRootFromSequence(left, nodeTypeInd - 1));
                ((BinaryOperationNode)node)
                        .setSecondNode(getTreeRootFromSequence(nodeTypeInd + 1, right));
                break;
            }
            default: node = new VariableNode(new VariableName(nodeTypeName));
        }

        return node;
    }
}
