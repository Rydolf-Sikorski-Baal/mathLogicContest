package Expression.SchemeDecorator;

import Expression.ExpressionTree;
import Expression.VariableName;

public interface ExpressionAsSchemeDecoratorInterface {
    ExpressionTree getExpression();
    void changeVariableToExpression(VariableName variableName, ExpressionTree expression);
}
