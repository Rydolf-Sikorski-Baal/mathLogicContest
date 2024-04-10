package Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaExpression {
    private List<ExpressionTree> hypotheses;
    //to make sure the hypotheses multiset between two metaExpressions match I use HashMap (the hash for hypothesis is basically the hash for a corresponding string)
    private final Map<ExpressionTree, Integer> hypothesesMultisetCounter = new HashMap<>();

    private ExpressionTree expression;

    public void setHypotheses(List<ExpressionTree> hypotheses){this.hypotheses = hypotheses;}
    public List<ExpressionTree> getHypotheses(){return this.hypotheses;}
    public Map<ExpressionTree, Integer> getHypothesesMultisetCounter(){return this.hypothesesMultisetCounter;}
    public void setExpression(ExpressionTree expression){this.expression = expression;}
    public ExpressionTree getExpression(){return this.expression;}

    private String getHypothesesString(){
        StringBuilder result = new StringBuilder();
        for (ExpressionTree currentStatement : this.hypotheses)
            result.append(currentStatement.toString()).append(',');
        if (result.length() > 0) result.delete(result.length() - 1, result.length());
        return result.toString();
    }

    public String toString() {
        return getHypothesesString()
                + "|-"
                + this.expression.toString()
                + '\n';
    }
}
