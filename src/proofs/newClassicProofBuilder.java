package proofs;

import Expression.*;
import MathOperations.LogicImplementation;
import Parcer.Parser;
import Expression.SchemeDecorator.ExpressionAsSchemeDecorator;
import Expression.SchemeDecorator.ExpressionAsSchemeDecoratorInterface;

import java.util.*;

public class newClassicProofBuilder extends AbstractNewProofBuilder {
    private final ArrayList<ExpressionTree> hypotheses = new ArrayList<>();
    private final ArrayList<ExpressionTree> axioms = new ArrayList<>();
    private ExpressionTree excludedHypothesis;
    private ExpressionTree finalStatement;

    private final ArrayList<ExpressionTree> statements = new ArrayList<>();

    @Override
    public AbstractNewProofBuilder setHypotheses(ArrayList<ExpressionTree> hypotheses, ExpressionTree excludedHypothesis) {
        this.excludedHypothesis = excludedHypothesis;
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis != excludedHypothesis)
                this.hypotheses.add(hypothesis);
        return this;
    }

    @Override
    public AbstractNewProofBuilder setAxioms() {
        Parser parser = Parser.getInstance();
        axioms.add(parser.getExpressionTree("_A -> (_B -> _A)"));
        axioms.add(parser.getExpressionTree("(_A -> _B) -> (_A -> _B -> _C) -> (_A -> _C))"));
        axioms.add(parser.getExpressionTree("_A -> _B -> _A & _B"));
        axioms.add(parser.getExpressionTree("_A & _B -> _A"));
        axioms.add(parser.getExpressionTree("_A & _B -> _B"));

        axioms.add(parser.getExpressionTree("_A -> _A | _B"));
        axioms.add(parser.getExpressionTree("_B -> _A | _B"));
        axioms.add(parser.getExpressionTree("(_A -> _C) -> (_B -> _C) -> (_A | _B -> _C"));
        axioms.add(parser.getExpressionTree("(_A -> _B) -> (_A -> !_B) -> !_A"));
        axioms.add(parser.getExpressionTree("!!_A->_A"));

        return this;
    }

    @Override
    public AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement) {
        Parser parser = Parser.getInstance();
        this.finalStatement = parser.getExpressionTree("_A -> _fSt");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(this.finalStatement);
        decorator.changeVariableToExpression(new VariableName("_A"), this.excludedHypothesis);
        decorator.changeVariableToExpression(new VariableName("_fSt"), finalStatement);
        this.finalStatement = decorator.getExpression();
        return this;
    }

    private boolean isOneOfAxioms(ExpressionTree statement){
        for (ExpressionTree axiom : axioms)
            if (axiom.tryAsSchemeFor(statement))
                return true;
        return false;
    }
    private boolean isOneOfHypotheses(ExpressionTree statement){
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis.equals(statement)) return true;
        return false;
    }
    @Override
    public AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> oldStatements, ExpressionTree hypA) {

        Map<ExpressionTree, ExpressionTree> modusPonensCandidates = new HashMap<>();
        Set<ExpressionTreeNode> deducted = new HashSet<>();
        for (ExpressionTree currentStatement : oldStatements){
            if (deducted.contains(currentStatement.root())) continue;
            processProofStatement(currentStatement, hypA, modusPonensCandidates, deducted);
            deducted.add(currentStatement.root());
        }
        return this;
    }

    private void processProofStatement(ExpressionTree currentStatement,
                                       ExpressionTree hypA,
                                       Map<ExpressionTree, ExpressionTree> modusPonensCandidates,
                                       Set<ExpressionTreeNode> deducted){
        ExpressionTree previous;
        if (currentStatement.root() instanceof BinaryOperationNode) {
            BinaryOperationNode node = (BinaryOperationNode) currentStatement.root();
            if (node.getNodeOperation() instanceof LogicImplementation) {
                ExpressionTree left = new ExpressionTree(node.getFirstNode(),
                        node.getFirstNode().getVariables());
                ExpressionTree right = new ExpressionTree(node.getSecondNode(),
                        node.getSecondNode().getVariables());

                modusPonensCandidates.put(right, left);
            }
        }

        if ((previous = tryToFindPreviousFast(modusPonensCandidates, currentStatement)) != null){
            BinaryOperationNode prRoot = (BinaryOperationNode) previous.root();

            ExpressionTree left = new ExpressionTree(prRoot.getFirstNode(),
                    prRoot.getFirstNode().getVariables());
            ExpressionTree right = new ExpressionTree(prRoot.getSecondNode(),
                    prRoot.getSecondNode().getVariables());

            addPreviouslyConstructed(hypA, left, right);
            return;
        }
        if (isOneOfAxioms(currentStatement)) { //аксиома
            addAxiomaticProof(hypA, currentStatement);
            return;
        }
        if (isOneOfHypotheses(currentStatement)) { //одна из гипотез
            addAxiomaticProof(hypA, currentStatement);
            return;
        }
        if (hypA.equals(currentStatement)) { //hypA
            addSelfImplication(hypA);
            return;
        }

        //если выводится из доказанного,
        //то нужно найти из чего оно выведено (StI -> cSt)
        if ((previous = tryToFindPrevious(deducted, currentStatement)) != null) {
            BinaryOperationNode prRoot = (BinaryOperationNode) previous.root();

            ExpressionTree left = new ExpressionTree(prRoot.getFirstNode(),
                    prRoot.getFirstNode().getVariables());
            ExpressionTree right = new ExpressionTree(prRoot.getSecondNode(),
                    prRoot.getSecondNode().getVariables());

            addPreviouslyConstructed(hypA, left, right);
        }
    }

    private ExpressionTree tryToFindPreviousFast(Map<ExpressionTree, ExpressionTree> modusPonensCandidates, ExpressionTree currentStatement) {
        return modusPonensCandidates.get(currentStatement);
    }

    private void addSelfImplication(ExpressionTree hypA){
        Parser parser = Parser.getInstance();
        VariableName AName = new VariableName("A");

        ExpressionTree firstExpr = parser.getExpressionTree("A -> (A -> A)");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(firstExpr);//hypA -> (hypA -> hypA)

        ExpressionTree secondExpr = parser.getExpressionTree("A -> ((A -> A) -> A)");
        decorator = new ExpressionAsSchemeDecorator(secondExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(secondExpr);//hypA -> ((hypA -> hypA) -> hypA)

        ExpressionTree thirdExpr = parser.getExpressionTree("(A -> (A -> A)) -> ((A -> ((A -> A) -> A)) -> (A -> A))");
        decorator = new ExpressionAsSchemeDecorator(thirdExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(thirdExpr);//( hypA -> ((hypA -> hypA) -> hypA) ) -> ( (hypA -> (hypA -> hypA)) -> (hypA -> hypA) )

        ExpressionTree forthExpr = parser.getExpressionTree("(A -> ((A -> A) -> A)) -> (A -> A)");
        decorator = new ExpressionAsSchemeDecorator(forthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(forthExpr);//(hypA -> (hypA -> hypA)) -> (hypA -> hypA)

        ExpressionTree fifthExpr = parser.getExpressionTree("A -> A");
        decorator = new ExpressionAsSchemeDecorator(fifthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(fifthExpr);//hypA -> hypA
    }

    private void addPreviouslyConstructed(ExpressionTree hypA, ExpressionTree left, ExpressionTree right){
        Parser parser = Parser.getInstance();
        ExpressionAsSchemeDecoratorInterface decorator;

        ExpressionTree firstExpression = parser.getExpressionTree("(_A->_StL)->(_A->_StL->_StR)->(_A->_StR)");
        decorator = new ExpressionAsSchemeDecorator(firstExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_StL"), left);
        decorator.changeVariableToExpression(new VariableName("_StR"), right);
        statements.add(firstExpression);

        ExpressionTree secondExpression = parser.getExpressionTree("(_A->_StL->_StR)->(_A->_StR)");
        decorator = new ExpressionAsSchemeDecorator(secondExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_StL"), left);
        decorator.changeVariableToExpression(new VariableName("_StR"), right);
        statements.add(secondExpression);

        ExpressionTree thirdExpression = parser.getExpressionTree("_A->_StR");
        decorator = new ExpressionAsSchemeDecorator(thirdExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_StL"), left);
        decorator.changeVariableToExpression(new VariableName("_StR"), right);
        statements.add(thirdExpression);
    }

    private void addAxiomaticProof(ExpressionTree hypA, ExpressionTree currentStatement){
        Parser parser = Parser.getInstance();

        ExpressionTree firstExpression = parser.getExpressionTree("_A -> _B -> _A");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), currentStatement);
        decorator.changeVariableToExpression(new VariableName("_B"), hypA);
        statements.add(decorator.getExpression());//cSt -> (hypA -> cSt))

        statements.add(currentStatement);

        ExpressionTree secondExpression = parser.getExpressionTree("_A -> _B");
        decorator = new ExpressionAsSchemeDecorator(secondExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_B"), currentStatement);
        statements.add(decorator.getExpression());//hypA -> cSt
    }

    private ExpressionTree tryToFindPrevious(Set<ExpressionTreeNode> deducted,
                                             ExpressionTree currentStatement){
        //we need (left) && (left->right) expressions
        //right == currentStatement
        for (ExpressionTreeNode previousRoot : deducted){
            if (previousRoot instanceof BinaryOperationNode) {
                BinaryOperationNode prRoot = (BinaryOperationNode) previousRoot;

                if (prRoot.getNodeOperation() instanceof LogicImplementation) {
                    if (currentStatement.root().equals(prRoot.getSecondNode())) {
                        for (ExpressionTreeNode previousLeft : deducted) {
                            if (previousLeft.equals(prRoot.getFirstNode()))
                                return new ExpressionTree(previousRoot, new VariablesList());
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Proof build() {
        return new Proof(this.hypotheses, this.axioms, this.finalStatement, this.statements);
    }
}
