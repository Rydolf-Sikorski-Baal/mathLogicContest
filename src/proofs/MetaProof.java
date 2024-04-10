package proofs;

import Expression.ExpressionTree;
import Expression.MetaExpression;

import java.util.ArrayList;

public class MetaProof {
    public MetaProof(ArrayList<ExpressionTree> axioms, ExpressionTree finalStatement, ArrayList<MetaExpression> statements) {
        this.axioms = axioms;
        this.statements = statements;
    }

    private final ArrayList<ExpressionTree> axioms;

    public ArrayList<ExpressionTree> axioms(){return axioms;}
    private final ArrayList<MetaExpression> statements;
    public ArrayList<MetaExpression> statements(){return statements;}

    @Override
    public String toString(){
        return getStatementsString(statements);
    }

    private String getStatementsString(ArrayList<MetaExpression> statements) {
        StringBuilder result = new StringBuilder();
        for (MetaExpression currentStatement : statements)
            result.append(currentStatement.toString()).append('\n');
        return result.toString();
    }
}
