package proofs;

import Expression.ExpressionTree;

public class ProofBuilderDirector {
    private AbstractNewProofBuilder builder;
    public ProofBuilderDirector(AbstractNewProofBuilder builder){
        this.builder = builder;
    }

    public Proof buildProof(Proof oldProof, ExpressionTree hypA){
        return builder
                .setHypotheses(oldProof.hypotheses(), hypA)
                .setAxioms()
                .setFinalStatement(oldProof.finalStatement())
                .rebuildProof(oldProof.statements(), hypA)
                .build();
    }
}
