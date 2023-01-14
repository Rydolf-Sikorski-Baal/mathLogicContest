package proofs;

import Expression.ExpressionTree;

import java.util.ArrayList;

public class newIntuitionistProofBuilder extends AbstractNewProofBuilder{
    @Override
    public AbstractNewProofBuilder setHypotheses(ArrayList<ExpressionTree> hypotheses, ExpressionTree excludedHypothesis) {
        return null;
    }

    @Override
    public AbstractNewProofBuilder setAxioms() {
        return null;
    }

    @Override
    public AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement) {
        return null;
    }

    @Override
    public AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> statements, ExpressionTree hypA) {
        return null;
    }

    @Override
    public Proof build() {
        return null;
    }
}
