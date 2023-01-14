package Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExpressionTreeNode {
    public abstract boolean getResult(SettedVariablesMap variablesMap);

    public abstract boolean tryAsSchemeFor(Object obj, Map<VariableName, ExpressionTreeNode> map);

    public abstract String toPrefixString();

    public VariablesList getVariables(){
        VariablesList variablesList = new VariablesList();
        setVariables(variablesList.getVariableList());
        return variablesList;
    }

    private void setVariables(List<VariableName> variableNames){
        if (this instanceof VariableNode){
            VariableNode node = (VariableNode) this;
            for (VariableName name : variableNames){
                if (name.equals(node.getVariableName())) return;
            }

            variableNames.add(node.getVariableName());
            return;
        }
        if (this instanceof UnaryOperationNode){
            UnaryOperationNode node = (UnaryOperationNode) this;
            node.getBoolNode().setVariables(variableNames);
            return;
        }
        if (this instanceof BinaryOperationNode){
            BinaryOperationNode node = (BinaryOperationNode) this;
            node.getFirstNode().setVariables(variableNames);
            node.getSecondNode().setVariables(variableNames);
        }
    }
}
