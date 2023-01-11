package proofs;

import Expression.ExpressionTree;

import java.util.ArrayList;

public class Proof {
    private final ArrayList<ExpressionTree> hypotheses;

    public Proof(ArrayList<ExpressionTree> hypotheses, ArrayList<ExpressionTree> axioms, ExpressionTree finalStatement, ArrayList<ExpressionTree> statements) {
        this.hypotheses = hypotheses;
        this.axioms = axioms;
        this.finalStatement = finalStatement;
        this.statements = statements;
    }

    public ArrayList<ExpressionTree> hypotheses(){return hypotheses;}
    private final ArrayList<ExpressionTree> axioms;
    public ArrayList<ExpressionTree> axioms(){return axioms;}
    private final ExpressionTree finalStatement;
    public ExpressionTree finalStatement(){return finalStatement;}
    private final ArrayList<ExpressionTree> statements;
    public ArrayList<ExpressionTree> statements(){return statements;}

    @Override
    public String toString(){
        return getHypothesesString(hypotheses) +
                "|-" +
                finalStatement.toString() +
                '\n' +
                getStatementsString(statements);
    }

    private String getStatementsString(ArrayList<ExpressionTree> statements) {
        StringBuilder result = new StringBuilder();
        for (ExpressionTree currentStatement : statements)
            result.append(currentStatement.toString()).append('\n');
        return result.toString();
    }

    private String getHypothesesString(ArrayList<ExpressionTree> hypotheses){
        StringBuilder result = new StringBuilder();
        for (ExpressionTree currentStatement : hypotheses)
            result.append(currentStatement.toString()).append(',');
        if (result.length() > 0) result.delete(result.length() - 1, result.length());
        return result.toString();
    }
}
