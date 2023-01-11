package Expression;

import java.util.HashMap;
import java.util.Map;

public class ExpressionTree {
    private final ExpressionTreeNode root;
    public ExpressionTreeNode root(){return root;}
    private final VariablesList variables;
    public VariablesList variables(){return variables;}

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
        return root.toString();
    }
}
