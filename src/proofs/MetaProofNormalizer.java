package proofs;

import Expression.BinaryOperationNode;
import Expression.ExpressionTree;
import Expression.ExpressionTreeNode;
import Expression.MetaExpression;
import MathOperations.LogicImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaProofNormalizer{
    public MetaProofNormalizer(){}

    private List<ExpressionTree> currentStatements;
    //current context
    private List<ExpressionTree> hypotheses;
    //to make sure the hypotheses multiset between two metaExpressions match I use HashMap (the hash for hypothesis is basically the hash for a corresponding string)
    private Map<ExpressionTree, Integer> hypothesesMultisetCounter = new HashMap<>();
    private MetaProof metaProof;

    private enum machineState{
        START,
        MIDDLE
    }
    private enum possibleRelationship{
        EQUAL,
        HYPOTHESIS_EXTRACTED,
        HYPOTHESIS_IMPLEMENTED
    }
    private Proof normalize(){
        this.minimizeMetaProof();

        List<MetaExpression> statements = metaProof.statements();

        machineState state = machineState.START;
        for (MetaExpression expression : statements){
            switch (state){
                case START: {
                    this.copyToTheProof(expression);
                    this.currentHypotheses = expression.getHypotheses();

                    state = machineState.MIDDLE;
                    break;
                }
                case MIDDLE: {
                    //it can possibly be not the last one to serve as a source for the deduction according to my knowledge
                    possibleRelationship result = this.compareHypotheses(expression);
                    if (result.equals(possibleRelationship.EQUAL)) copyToTheProof(expression);
                    if (result.equals(possibleRelationship.HYPOTHESIS_EXTRACTED)) this.applyDirectDeduction(expression);
                    if (result.equals(possibleRelationship.HYPOTHESIS_IMPLEMENTED)) this.applyReverseDeduction(expression);
                }
            }
        }

        return new Proof(this.currentHypotheses, this.axioms, this.finalStatement, this.currentStatements);
    }

    //alongside with minimization - breaks necessary deduction into parts in order to simplify future processing
    private void minimizeMetaProof() {

    }

    private void applyReverseDeduction(MetaExpression expression) {
        ExpressionTree implementedHypothesis = null;
        for (Map.Entry<ExpressionTree, Integer> entry : expression.getHypothesesMultisetCounter().entrySet())
            if (!entry.getValue().equals(this.hypothesesMultisetCounter.get(entry.getKey()))){
                implementedHypothesis = entry.getKey();
                break;
            }

        if (implementedHypothesis == null)
            throw new RuntimeException("expression supplied for reverse deduction has no differential in context");

        //add new hypothesis at the start
        ArrayList<ExpressionTree> newStatements = new ArrayList<>();
        newStatements.add(implementedHypothesis);
        for (ExpressionTree statement : this.currentStatements){
            newStatements.add(statement);
            if (!(statement.root() instanceof BinaryOperationNode)) continue;
            BinaryOperationNode node = (BinaryOperationNode) statement.root();

            if (!(node.getNodeOperation() instanceof LogicImplementation)) continue;
            if (!node.getFirstNode().equals(implementedHypothesis.root())) continue;
            newStatements.add(new ExpressionTree(node.getSecondNode(), node.getSecondNode().getVariables()));
        }

        this.currentStatements = newStatements;
        this.currentHypotheses = expression.getHypotheses();
        this.hypothesesMultisetCounter = expression.getHypothesesMultisetCounter();
    }

    private void applyDirectDeduction(MetaExpression expression) {
        ExpressionTree tree = expression.getExpression();
        ExpressionTreeNode root = tree.root();

        if (!(root instanceof BinaryOperationNode))
            throw new RuntimeException("expression supplied for direct deduction has no binary root");
        BinaryOperationNode binaryRoot = (BinaryOperationNode) root;
        if (!(binaryRoot.getNodeOperation() instanceof LogicImplementation))
            throw new RuntimeException("expression supplied for direct deduction has no implementation root");

        ExpressionTree extractedHypothesis = null;
        for (Map.Entry<ExpressionTree, Integer> entry : this.hypothesesMultisetCounter.entrySet())
            if (!entry.getValue().equals(expression.getHypothesesMultisetCounter().get(entry.getKey()))){
                extractedHypothesis = entry.getKey();
                break;
            }

        if (extractedHypothesis == null)
                throw new RuntimeException("expression supplied for direct deduction has no differential in context");

        ProofBuilderDirector director = new ProofBuilderDirector(new newClassicProofBuilder());
        director.buildProof(this.currentProof, extractedHypothesis);

        this.hypotheses = expression.getHypotheses();
        this.hypothesesMultisetCounter = expression.getHypothesesMultisetCounter();
    }

    List<ExpressionTree> currentHypotheses;
    private possibleRelationship compareHypotheses(MetaExpression expression) {
        for (Map.Entry<ExpressionTree, Integer> entry : expression.getHypothesesMultisetCounter().entrySet()){
            int currentValue = this.hypothesesMultisetCounter.getOrDefault(entry.getKey(), 0);
            if (entry.getValue() > currentValue) return possibleRelationship.HYPOTHESIS_IMPLEMENTED;
        }

        for (Map.Entry<ExpressionTree, Integer> entry : this.hypothesesMultisetCounter.entrySet()){
            int currentValue = expression.getHypothesesMultisetCounter().getOrDefault(entry.getKey(), 0);
            if (entry.getValue() > currentValue) return possibleRelationship.HYPOTHESIS_EXTRACTED;
        }

        return possibleRelationship.EQUAL;
    }

    private void copyToTheProof(MetaExpression expression) {
        this.currentStatements.add(expression.getExpression());
    }
}
