package Expression.SchemeDecorator;

import Expression.ExpressionTree;

public abstract class AbstractExpressionAsSchemeDecorator implements ExpressionAsSchemeDecoratorInterface {
    public AbstractExpressionAsSchemeDecorator(ExpressionTree expression){
        this.expression = expression;
    }

    private final ExpressionTree expression;

    public ExpressionTree getExpression(){return expression;}
}
