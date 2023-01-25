package Expression.SchemeDecorator;

import Expression.*;

public class ExpressionAsSchemeDecorator extends AbstractExpressionAsSchemeDecorator{
    public ExpressionAsSchemeDecorator(ExpressionTree expression) {
        super(expression);
    }

    @Override
    public void changeVariableToExpression(VariableName variableName, ExpressionTree expression) {
        VariablesList list = super.getExpression().variables();
        for (VariableName currentVariableName : list.getVariableList())
            if (currentVariableName.equals(variableName)) {
                changeCheckedVariableToExpression(variableName, expression, this.getExpression().root());
                this.getExpression().setVariables(this.getExpression().root().getVariables());
                return;
            }
    }

    @Override
    public void changeVariableToExpression(VariableName variableName, ExpressionTreeNode expression) {
        ExpressionTree currExpression = new ExpressionTree(expression, expression.getVariables());
        changeVariableToExpression(variableName, currExpression);
    }

    private void changeCheckedVariableToExpression(VariableName variableName, ExpressionTree expression, ExpressionTreeNode node){
        if(node instanceof UnaryOperationNode){
            UnaryOperationNode currentNode = (UnaryOperationNode) node;
            if (currentNode.getBoolNode() instanceof VariableNode){
                VariableNode boolNode = (VariableNode) currentNode.getBoolNode();
                if (boolNode.getVariableName().equals(variableName)){
                    currentNode.setBoolNode(expression.root());
                    return;
                }
            }

            changeCheckedVariableToExpression(variableName, expression, currentNode.getBoolNode());
        }

        if (node instanceof BinaryOperationNode){
            BinaryOperationNode currentNode = (BinaryOperationNode) node;

            if (currentNode.getFirstNode() instanceof VariableNode){
                VariableNode firstNode = (VariableNode) currentNode.getFirstNode();
                if (firstNode.getVariableName().equals(variableName))
                    currentNode.setFirstNode(expression.root());
            }   else {
                changeCheckedVariableToExpression(variableName, expression, currentNode.getFirstNode());
            }

            if (currentNode.getSecondNode() instanceof VariableNode){
                VariableNode secondNode = (VariableNode) currentNode.getSecondNode();
                if (secondNode.getVariableName().equals(variableName))
                    currentNode.setSecondNode(expression.root());
            }   else{
                changeCheckedVariableToExpression(variableName, expression, currentNode.getSecondNode());
            }
        }
    }
}
