package Expression.SchemeDecorator;

import Expression.ExpressionTree;
import Expression.ExpressionTreeNode;
import Expression.VariableName;

public interface ExpressionAsSchemeDecoratorInterface {
    ExpressionTree getExpression();
    void changeVariableToExpression(VariableName variableName, ExpressionTree expression);
    void changeVariableToExpression(VariableName variableName, ExpressionTreeNode expression);
}
