package proofs;

import Expression.*;
import Expression.MathLogic.MathLogicAxioms;
import MathOperations.LogicImplementation;
import Parcer.Parser;
import Expression.SchemeDecorator.ExpressionAsSchemeDecorator;
import Expression.SchemeDecorator.ExpressionAsSchemeDecoratorInterface;

import java.util.*;

public class newClassicProofBuilder extends AbstractNewProofBuilder {
    private final ArrayList<ExpressionTree> hypotheses = new ArrayList<>();
    private List<ExpressionTree> axioms = new ArrayList<>();
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
        this.axioms = MathLogicAxioms.getClassicAxiomsList();

        return this;
    }

    @Override
    public AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement) {
        Parser parser = Parser.getInstance();
        this.finalStatement = parser.getExpressionTree("_A -> _fSt");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(this.finalStatement);
        decorator.changeVariableToExpression(new VariableName("_A"), this.excludedHypothesis.getDeepCopy());
        decorator.changeVariableToExpression(new VariableName("_fSt"), finalStatement.getDeepCopy());
        this.finalStatement = decorator.getExpression();
        return this;
    }

    private boolean isOneOfAxioms(ExpressionTree statement){
        for (ExpressionTree axiom : axioms)
            if (axiom.tryAsSchemeFor(statement))
                return true;
        return false;
    }
    private boolean isOneOfHypotheses(ExpressionTree statement){
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis.equals(statement)) return true;
        return false;
    }
    @Override
    public AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> oldStatements, ExpressionTree hypA) {

        Map<ExpressionTreeNode, ExpressionTreeNode> modusPonensCandidates = new HashMap<>();
        Set<ExpressionTreeNode> deducted = new HashSet<>();
        BinaryOperationNode finalRoot = (BinaryOperationNode) finalStatement.root();
        ExpressionTreeNode previousFinal = finalRoot.getSecondNode();

        for (ExpressionTree currentStatement : oldStatements){
            if (deducted.contains(currentStatement.root())) continue;

            processProofStatement(currentStatement, hypA, modusPonensCandidates, deducted);

            if (currentStatement.root() instanceof BinaryOperationNode)
                if (((BinaryOperationNode) currentStatement.root()).getNodeOperation() instanceof LogicImplementation) {
                    BinaryOperationNode currentRoot = (BinaryOperationNode) currentStatement.root();
                    modusPonensCandidates.put(currentRoot.getSecondNode(), currentRoot.getFirstNode());
                }

            deducted.add(currentStatement.root());
            if (currentStatement.root().equals(previousFinal)) break;
        }
        return this;
    }

    private void processProofStatement(ExpressionTree currentStatement,
                                       ExpressionTree hypA,
                                       Map<ExpressionTreeNode, ExpressionTreeNode> modusPonensCandidates,
                                       Set<ExpressionTreeNode> deducted){
        ExpressionTree previous;
        if ((previous = tryToFindPreviousFast(currentStatement, deducted, modusPonensCandidates)) != null){
            BinaryOperationNode prRoot = (BinaryOperationNode) previous.root();

            ExpressionTree left = new ExpressionTree(prRoot.getFirstNode(),
                    prRoot.getFirstNode().getVariables());

            addPreviouslyConstructed(hypA, left, currentStatement.getDeepCopy());
            return;
        }
        if (isOneOfAxioms(currentStatement)) { //аксиома
            addAxiomaticProof(hypA, currentStatement);
            return;
        }
        if (isOneOfHypotheses(currentStatement)) { //одна из гипотез
            addAxiomaticProof(hypA, currentStatement);
            return;
        }
        if (hypA.equals(currentStatement)) { //hypA
            addSelfImplication(hypA);
            return;
        }

        //если выводится из доказанного,
        //то нужно найти из чего оно выведено (StI -> cSt)
        if ((previous = tryToFindPrevious(deducted, currentStatement)) != null) {
            BinaryOperationNode prRoot = (BinaryOperationNode) previous.root();

            ExpressionTree left = new ExpressionTree(prRoot.getFirstNode(),
                    prRoot.getFirstNode().getVariables());

            addPreviouslyConstructed(hypA, left, currentStatement.getDeepCopy());
        }
    }

    private void processSelfImplication(ExpressionTree statement) {
        BinaryOperationNode root = (BinaryOperationNode) statement.root();
        ExpressionTreeNode newRoot = root.getFirstNode();
        ExpressionTree newTree = new ExpressionTree(newRoot, newRoot.getVariables());
        addSelfImplication(newTree);
    }

    private boolean isItSelfImplication(ExpressionTree statement) {
        if (!(statement.root() instanceof BinaryOperationNode)) return false;
        BinaryOperationNode root = (BinaryOperationNode) statement.root();
        if (!(root.getNodeOperation() instanceof LogicImplementation)) return false;
        return root.getFirstNode().equals(root.getSecondNode());
    }

    private void addSelfImplication(ExpressionTree hypA){
        Parser parser = Parser.getInstance();
        VariableName AName = new VariableName("_A");

        ExpressionTree firstExpr = parser.getExpressionTree("_A -> (_A -> _A)");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(firstExpr);//hypA -> (hypA -> hypA)

        ExpressionTree secondExpr = parser.getExpressionTree("_A -> ((_A -> _A) -> _A)");
        decorator = new ExpressionAsSchemeDecorator(secondExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(secondExpr);//hypA -> ((hypA -> hypA) -> hypA)

        ExpressionTree thirdExpr = parser.getExpressionTree("(_A -> (_A -> _A)) -> ((_A -> ((_A -> _A) -> _A)) -> (_A -> _A))");
        decorator = new ExpressionAsSchemeDecorator(thirdExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(thirdExpr);//( hypA -> ((hypA -> hypA) -> hypA) ) -> ( (hypA -> (hypA -> hypA)) -> (hypA -> hypA) )

        ExpressionTree forthExpr = parser.getExpressionTree("(_A -> ((_A -> _A) -> _A)) -> (_A -> _A)");
        decorator = new ExpressionAsSchemeDecorator(forthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(forthExpr);//(hypA -> (hypA -> hypA)) -> (hypA -> hypA)

        ExpressionTree fifthExpr = parser.getExpressionTree("_A -> _A");
        decorator = new ExpressionAsSchemeDecorator(fifthExpr);
        decorator.changeVariableToExpression(AName, hypA);
        statements.add(fifthExpr);//hypA -> hypA
    }

    private void addPreviouslyConstructed(ExpressionTree hypA, ExpressionTree left, ExpressionTree right){
        Parser parser = Parser.getInstance();
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
        statements.add(secondExpression.getDeepCopy());

        ExpressionTree thirdExpression = parser.getExpressionTree("_A->_StR");
        decorator = new ExpressionAsSchemeDecorator(thirdExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_StL"), left);
        decorator.changeVariableToExpression(new VariableName("_StR"), right);
        statements.add(thirdExpression);
    }

    private void addAxiomaticProof(ExpressionTree hypA, ExpressionTree currentStatement){
        Parser parser = Parser.getInstance();

        ExpressionTree firstExpression = parser.getExpressionTree("_A -> _B -> _A");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(firstExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), currentStatement);
        decorator.changeVariableToExpression(new VariableName("_B"), hypA);
        statements.add(decorator.getExpression());//cSt -> (hypA -> cSt))

        statements.add(currentStatement.getDeepCopy());

        ExpressionTree secondExpression = parser.getExpressionTree("_A -> _B");
        decorator = new ExpressionAsSchemeDecorator(secondExpression);
        decorator.changeVariableToExpression(new VariableName("_A"), hypA);
        decorator.changeVariableToExpression(new VariableName("_B"), currentStatement);
        statements.add(decorator.getExpression());//hypA -> cSt
    }

    private ExpressionTree tryToFindPreviousFast(ExpressionTree current,
                                                 Set<ExpressionTreeNode> deducted,
                                                 Map<ExpressionTreeNode, ExpressionTreeNode> candidates){
        ExpressionTreeNode left = candidates.get(current.root());
        if (left == null) return null;
        if (!deducted.contains(left)) return null;

        ExpressionAsSchemeDecoratorInterface decorator =
                new ExpressionAsSchemeDecorator(Parser.getInstance().getExpressionTree("_left -> _right"));
        decorator.changeVariableToExpression(new VariableName("_left"), left);
        decorator.changeVariableToExpression(new VariableName("_right"), current);
        return decorator.getExpression();
    }
    private ExpressionTree tryToFindPrevious(Set<ExpressionTreeNode> deducted,
                                             ExpressionTree currentStatement){
        //we need (left) && (left->right) expressions
        //right == currentStatement
        ExpressionAsSchemeDecoratorInterface decorator;
        for (ExpressionTreeNode previousRoot : deducted){
            decorator =
                    new ExpressionAsSchemeDecorator(Parser.getInstance().getExpressionTree("left -> current"));
            decorator.changeVariableToExpression(new VariableName("left"), previousRoot);
            decorator.changeVariableToExpression(new VariableName("current"), currentStatement);

            if (deducted.contains(decorator.getExpression().root()))
                return decorator.getExpression();
        }
        return null;
    }

    @Override
    public Proof build() {
        return new Proof(this.hypotheses, this.axioms, this.finalStatement, this.statements);
    }
}
