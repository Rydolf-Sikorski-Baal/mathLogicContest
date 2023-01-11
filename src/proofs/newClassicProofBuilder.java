package proofs;

import Expression.*;
import Parcer.Parser;
import Expression.SchemeDecorator.ExpressionAsSchemeDecorator;
import Expression.SchemeDecorator.ExpressionAsSchemeDecoratorInterface;

import java.util.ArrayList;

public class newClassicProofBuilder extends AbstractNewProofBuilder {
    private final ArrayList<ExpressionTree> hypotheses = new ArrayList<>();
    private final ArrayList<ExpressionTree> axioms = new ArrayList<>();
    private ExpressionTree excludedHypothesis;
    private ExpressionTree finalStatement;

    private final ArrayList<ExpressionTree> statements = new ArrayList<>();

    @Override
    public AbstractNewProofBuilder setHypotheses(ArrayList<ExpressionTree> hypotheses, ExpressionTree excludedHypothesis) {
        this.excludedHypothesis = excludedHypothesis;
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis != excludedHypothesis)
                this.hypotheses.add(hypothesis);
        return this;
    }

    @Override
    public AbstractNewProofBuilder setAxioms() {
        Parser parser = Parser.getInstance();
        axioms.add(parser.getExpressionTree("_A -> _B -> _A"));
        axioms.add(parser.getExpressionTree("(_A -> _B) -> (_A -> _B -> _C) -> (_A -> _C))"));
        axioms.add(parser.getExpressionTree("_A -> _B -> _A & _B"));
        axioms.add(parser.getExpressionTree("_A & _B -> _A"));
        axioms.add(parser.getExpressionTree("_A & _B -> _B"));

        axioms.add(parser.getExpressionTree("_A -> _A | _B"));
        axioms.add(parser.getExpressionTree("_B -> _A | _B"));
        axioms.add(parser.getExpressionTree("(_A -> _C) -> (_B -> _C) -> (_A | _B -> _C"));
        axioms.add(parser.getExpressionTree("(_A -> _B) -> (_A -> !_B) -> !_A"));
        axioms.add(parser.getExpressionTree("!!_A->_A"));

        return this;
    }

    @Override
    public AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement) {
        Parser parser = Parser.getInstance();
        this.finalStatement = parser.getExpressionTree("_A -> _fSt");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(this.finalStatement);
        decorator.changeVariableToExpression(new VariableName("_A"), this.excludedHypothesis);
        decorator.changeVariableToExpression(new VariableName("_fSt"), finalStatement);
        this.finalStatement = decorator.getExpression();
        return this;
    }

    private boolean isOneOfAxioms(ExpressionTree statement){
        for (ExpressionTree axiom : axioms)
            if (axiom.tryAsSchemeFor(statement)) return true;
        return false;
    }
    private boolean isOneOfHypotheses(ExpressionTree statement){
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis.equals(statement)) return true;
        return false;
    }
    @Override
    public AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> oldStatements, ExpressionTree hypA) {
        Parser parser = Parser.getInstance();
        for (ExpressionTree currentStatement : oldStatements){
            if (isOneOfAxioms(currentStatement)) { //аксиома
                ExpressionTree firstExpression = parser.getExpressionTree("_A -> _B -> _A");
                ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), currentStatement);
                decorator.changeVariableToExpression(new VariableName("_B"), hypA);
                statements.add(decorator.getExpression());//cSt -> (hypA -> cSt))

                statements.add(currentStatement);

                ExpressionTree secondExpression = parser.getExpressionTree("_A -> _B");
                decorator = new ExpressionAsSchemeDecorator(secondExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), hypA);
                decorator.changeVariableToExpression(new VariableName("_B"), currentStatement);
                statements.add(decorator.getExpression());//hypA -> cSt


                continue;
            }
            if (isOneOfHypotheses(currentStatement)) { //одна из гипотез
                ExpressionTree firstExpression = parser.getExpressionTree("_A -> _B -> _A");
                ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), currentStatement);
                decorator.changeVariableToExpression(new VariableName("_B"), hypA);
                statements.add(decorator.getExpression());//cSt -> (hypA -> cSt))

                statements.add(currentStatement);

                ExpressionTree secondExpression = parser.getExpressionTree("_A -> _B");
                decorator = new ExpressionAsSchemeDecorator(secondExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), hypA);
                decorator.changeVariableToExpression(new VariableName("_B"), currentStatement);
                statements.add(decorator.getExpression());//hypA -> cSt

                continue;
            }
            if (hypA.equals(currentStatement)) { //hypA
                addSelfImplication(hypA);

                continue;
            }

            //выводится из доказанного
            // нужно найти из чего оно выведено (StI -> cSt)
            ExpressionTreeNode previouslyDeductedLeft = null;
            ExpressionTreeNode previouslyDeductedRight = null;
            VariablesList prVariables = null;
            for (ExpressionTree prStatement : statements){
                if (prStatement.root().getClass() != BinaryOperationNode.class) continue;
                ExpressionTreeNode prLeft  = ((BinaryOperationNode)prStatement.root()).getFirstNode();
                ExpressionTreeNode prRight = ((BinaryOperationNode)prStatement.root()).getSecondNode();

                if (currentStatement.root().equals(prRight)) {
                    previouslyDeductedLeft  = prLeft;
                    previouslyDeductedRight = prRight;
                    prVariables = prStatement.variables();
                    break;
                }
            }

            if (previouslyDeductedLeft != null && currentStatement.root().equals(previouslyDeductedRight)) {
                ExpressionTree left = new ExpressionTree(previouslyDeductedLeft, prVariables);
                ExpressionTree right = new ExpressionTree(previouslyDeductedRight, prVariables);

                ExpressionAsSchemeDecoratorInterface decorator;

                ExpressionTree firstExpression = parser.getExpressionTree("(_A->_StL)->(_A->_StL->_StR)->(_A->_StR)");
                decorator = new ExpressionAsSchemeDecorator(firstExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), hypA);
                decorator.changeVariableToExpression(new VariableName("_StL"), left);
                decorator.changeVariableToExpression(new VariableName("_StR"), right);
                statements.add(firstExpression);

                ExpressionTree secondExpression = parser.getExpressionTree("(_A->_StL->_StR)->(_A->_StR)");
                decorator = new ExpressionAsSchemeDecorator(secondExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), hypA);
                decorator.changeVariableToExpression(new VariableName("_StL"), left);
                decorator.changeVariableToExpression(new VariableName("_StR"), right);
                statements.add(secondExpression);

                ExpressionTree thirdExpression = parser.getExpressionTree("_A->_StR");
                decorator = new ExpressionAsSchemeDecorator(thirdExpression);
                decorator.changeVariableToExpression(new VariableName("_A"), hypA);
                decorator.changeVariableToExpression(new VariableName("_StL"), left);
                decorator.changeVariableToExpression(new VariableName("_StR"), right);
                statements.add(thirdExpression);
            }
        }
        return this;
    }

    private void addSelfImplication(ExpressionTree hypA){
        Parser parser = Parser.getInstance();
        VariableName AName = new VariableName("A");

        ExpressionTree firstExpr = parser.getExpressionTree("A -> (A -> A)");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(firstExpr);//hypA -> (hypA -> hypA)

        ExpressionTree secondExpr = parser.getExpressionTree("A -> ((A -> A) -> A)");
        decorator = new ExpressionAsSchemeDecorator(secondExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(secondExpr);//hypA -> ((hypA -> hypA) -> hypA)

        ExpressionTree thirdExpr = parser.getExpressionTree("(A -> (A -> A)) -> ((A -> ((A -> A) -> A)) -> (A -> A))");
        decorator = new ExpressionAsSchemeDecorator(thirdExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(thirdExpr);//( hypA -> ((hypA -> hypA) -> hypA) ) -> ( (hypA -> (hypA -> hypA)) -> (hypA -> hypA) )

        ExpressionTree forthExpr = parser.getExpressionTree("(A -> ((A -> A) -> A)) -> (A -> A)");
        decorator = new ExpressionAsSchemeDecorator(forthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(forthExpr);//(hypA -> (hypA -> hypA)) -> (hypA -> hypA)

        ExpressionTree fifthExpr = parser.getExpressionTree("A -> A");
        decorator = new ExpressionAsSchemeDecorator(fifthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(fifthExpr);//hypA -> hypA
    }

    @Override
    public Proof build() {
        return new Proof(this.hypotheses, this.axioms, this.finalStatement, this.statements);
    }
}
