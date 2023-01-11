package proofs;

import Expression.ExpressionTree;

import java.util.ArrayList;

public abstract class AbstractNewProofBuilder {
    public abstract AbstractNewProofBuilder setHypotheses(ArrayList<ExpressionTree> hypotheses, ExpressionTree excludedHypothesis);
    public abstract AbstractNewProofBuilder setAxioms();
    public abstract AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement);
    public abstract AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> statements, ExpressionTree hypA);

    public abstract Proof build();
}
