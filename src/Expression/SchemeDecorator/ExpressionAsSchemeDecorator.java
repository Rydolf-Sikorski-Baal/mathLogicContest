package Expression.SchemeDecorator;

import Expression.*;

public class ExpressionAsSchemeDecorator extends AbstractExpressionAsSchemeDecorator{
    public ExpressionAsSchemeDecorator(ExpressionTree expression) {
        super(expression);
    }

    @Override
    public boolean changeVariableToExpression(VariableName variableName, ExpressionTree expression) {
        for (VariableName currentVariableName : super.getExpression().variables().getVariableList())
            if (currentVariableName.equals(variableName)) {
                changeCheckedVariableToExpression(variableName, expression, this.getExpression().root());
                return true;
            }
        return false;
    }

    private void changeCheckedVariableToExpression(VariableName variableName, ExpressionTree expression, ExpressionTreeNode node){
        if(node.getClass() == UnaryOperationNode.class){
            if ((((UnaryOperationNode) node).getBoolNode()).equals(new VariableNode(variableName))){
                ((UnaryOperationNode) node).setBoolNode(expression.root());
                return;
            }

            changeCheckedVariableToExpression(variableName, expression, ((UnaryOperationNode) node).getBoolNode());
        }

        if (node.getClass() == BinaryOperationNode.class){
            if ((((BinaryOperationNode) node).getFirstNode()).equals(new VariableNode(variableName))){
                ((BinaryOperationNode) node).setFirstNode(expression.root());
            }else {
                changeCheckedVariableToExpression(variableName, expression, ((BinaryOperationNode) node).getFirstNode());
            }

            if ((((BinaryOperationNode) node).getSecondNode()).equals(new VariableNode(variableName))){
                ((BinaryOperationNode) node).setSecondNode(expression.root());
            }else{
                changeCheckedVariableToExpression(variableName, expression, ((BinaryOperationNode) node).getSecondNode());
            }
        }
    }
}
