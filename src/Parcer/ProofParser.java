package Parcer;

import Expression.ExpressionTree;
import proofs.Proof;

import java.util.ArrayList;

public class ProofParser {
    private static ProofParser instance;
    private ProofParser(){}
    public static ProofParser getInstance(){
        if (instance == null) instance = new ProofParser();
        return instance;
    }

    Integer ind;
    public Proof parseProofFromString(String input){
        ind = 0;
        ArrayList<ExpressionTree> axioms = constructClassicAxioms();
        ArrayList<ExpressionTree> hypotheses = parseHypotheses(input);
        ExpressionTree finalStatement = parseFinalStatement(input);
        ArrayList<ExpressionTree> statements = parseStatements(input);

        return new Proof(hypotheses, axioms, finalStatement, statements);
    }

    Parser parser = Parser.getInstance();
    private ArrayList<ExpressionTree> constructClassicAxioms() {
        ArrayList<ExpressionTree> axioms = new ArrayList<>();

        axioms.add(parser.getExpressionTree("A -> ( B -> A)"));
        axioms.add(parser.getExpressionTree("(A -> (B -> C)) -> ((A -> B) -> (A -> C))"));
        axioms.add(parser.getExpressionTree("(!A -> !B) -> (B -> A)"));
        axioms.add(parser.getExpressionTree("(A & B) -> (!A -> B)"));
        axioms.add(parser.getExpressionTree("(A & B) -> !(!A & !B))"));

        return axioms;
    }

    private ArrayList<ExpressionTree> parseHypotheses(String input) {
        ArrayList<ExpressionTree> hypotheses = new ArrayList<>();

        StringBuilder currentHypothesisString = new StringBuilder();
        while (((int)input.charAt(ind) != 124) && ((input.charAt(ind) != '|')) || (input.charAt(ind + 1) != '-')){
            if (input.charAt(ind) == ',') {
                hypotheses.add(parser.getExpressionTree(currentHypothesisString.toString()));
                currentHypothesisString = new StringBuilder();
                ind++;
            }

            currentHypothesisString.append(input.charAt(ind));
            ind++;
            if (ind == input.length()) break;
        }
        if (isNotUseless(currentHypothesisString.toString()))
            hypotheses.add(parser.getExpressionTree(currentHypothesisString.toString()));

        ind += 2;

        return hypotheses;
    }

    private static boolean isNotUseless(String line) {
        for (char ch : line.toCharArray())
            if (!isEmpty(ch)) return true;
        return false;
    }

    private static boolean isEmpty(char ch){
        return (ch == ' ') || (ch == '\n') || (ch == '\0') || (ch == '\u0009') || (ch == '\r');
    }

    private ExpressionTree parseFinalStatement(String input) {
        StringBuilder builder = new StringBuilder();

        while (input.charAt(ind) != '\n'){
            builder.append(input.charAt(ind));
            ind++;
        }
        ind++;

        return parser.getExpressionTree(builder.toString());
    }

    private ArrayList<ExpressionTree> parseStatements(String input) {
        ArrayList<ExpressionTree> statements = new ArrayList<>();

        while (ind <= input.length() - 1){
            StringBuilder currentStatementString = new StringBuilder();

            boolean initialSectionOfStatementPassed = false;
            while ((input.charAt(ind) != '\n') && (input.charAt(ind) != '\0') && (ind <= input.length() - 1)) {
                if (initialSectionOfStatementPassed) currentStatementString.append(input.charAt(ind));

                if (ind > 0 && input.charAt(ind - 1) == '|' && input.charAt(ind) == '-') initialSectionOfStatementPassed = true;
                ind++;
                if (ind == input.length()) break;
            }
            statements.add(parser.getExpressionTree(currentStatementString.toString()));
            ind++;
        }

        return statements;
    }
}
