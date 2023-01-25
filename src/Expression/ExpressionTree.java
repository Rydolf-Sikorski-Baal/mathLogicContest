package Expression;

import java.util.HashMap;
import java.util.Map;

public class ExpressionTree {
    private final ExpressionTreeNode root;
    public ExpressionTreeNode root(){return root;}
    private VariablesList variables;
    public VariablesList variables(){return variables;}
    public void setVariables(VariablesList variables){this.variables = variables;}

    public ExpressionTree(ExpressionTreeNode root, VariablesList variables) {
        this.root = root;
        this.variables = variables;
    }

    public boolean checkExpression(SettedVariablesMap variablesMap){
        return root.getResult(variablesMap);
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
        return this.root.equals(((ExpressionTree) obj).root);
    }

    public boolean tryAsSchemeFor(Object obj){
        Map<VariableName, ExpressionTreeNode> map = new HashMap<>();
        for (VariableName variableName : this.variables.getVariableList())
            map.put(variableName, null);

        if (obj.getClass() != this.getClass()) return false;

        boolean result = root.tryAsSchemeFor(((ExpressionTree) obj).root, map);

        for (VariableName variableName : this.variables.getVariableList())
            if (map.get(variableName) == null) result = false;

        return result;
    }

    @Override
    public String toString(){
        String result = root.toString();
        return result.length() > 1 ? result.substring(1, result.length() - 1) : result;
    }

    @Override
    public int hashCode(){
        return this.toString().hashCode();
    }

    public String toPrefixString(){
        return this.root.toPrefixString();
    }

    public ExpressionTree getDeepCopy(){
        ExpressionTreeNode newRoot = this.root.getDeepCopy();
        VariablesList newVariables = newRoot.getVariables();
        return new ExpressionTree(newRoot, newVariables);
    }
}
