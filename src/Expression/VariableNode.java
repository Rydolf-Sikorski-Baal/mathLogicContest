package Expression;

import java.util.Map;

public class VariableNode extends ExpressionTreeNode{
    private final VariableName variableName;

    public VariableName getVariableName(){return variableName;}

    public VariableNode(VariableName variableName) {
        this.variableName = variableName;
    }

    @Override
    public boolean getResult(SettedVariablesMap variablesMap) {
        return variablesMap.variableMap().get(variableName).value();
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
        return this.variableName
                .equals(
                        ((VariableNode) obj).getVariableName()
                );
    }

    public boolean tryAsSchemeFor(Object obj, Map<VariableName, ExpressionTreeNode> constructedMap){
        if ((constructedMap.get(this.variableName)) == null){
            constructedMap.put(this.variableName, (ExpressionTreeNode)obj);
            return true;
        }

        return constructedMap.get(this.variableName).equals(obj);
    }

    @Override
    public String toString(){
        return this.variableName.name();
    }

    @Override
    public String toPrefixString(){
        return this.toString();
    }

    @Override
    public int hashCode(){return this.variableName.hashCode();}
}
