package proofs;

import Expression.ExpressionTree;
import Expression.SchemeDecorator.ExpressionAsSchemeDecorator;
import Expression.SchemeDecorator.ExpressionAsSchemeDecoratorInterface;
import Expression.VariableName;
import Parcer.Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class newIntuitionistProofBuilder extends AbstractNewProofBuilder{
    private ArrayList<ExpressionTree> hypotheses;
    private final ArrayList<ExpressionTree> axioms = new ArrayList<>();
    private ExpressionTree tenthAxiom;
    private ExpressionTree finalStatement;

    private final ArrayList<ExpressionTree> statements = new ArrayList<>();

    @Override
    public AbstractNewProofBuilder setHypotheses(ArrayList<ExpressionTree> hypotheses, ExpressionTree excludedHypothesis) {
        this.hypotheses = hypotheses;
        return this;
    }

    @Override
    public AbstractNewProofBuilder setAxioms() {
        Parser parser = Parser.getInstance();
        axioms.add(parser.getExpressionTree("a -> (b -> a)"));
        axioms.add(parser.getExpressionTree("(a -> b) -> (a -> b -> c) -> (a -> c)"));
        axioms.add(parser.getExpressionTree("a -> (b -> a & b)"));
        axioms.add(parser.getExpressionTree("a & b -> a"));
        axioms.add(parser.getExpressionTree("a & b -> b"));

        axioms.add(parser.getExpressionTree("a -> (a | b)"));
        axioms.add(parser.getExpressionTree("b -> (a | b)"));
        axioms.add(parser.getExpressionTree("(a -> c) -> (b -> c) -> ((a | b) -> c)"));
        axioms.add(parser.getExpressionTree("(a -> b) -> (a -> !b) -> !a"));
        axioms.add(parser.getExpressionTree("a -> (!a -> b)"));

        return this;
    }

    @Override
    public AbstractNewProofBuilder setFinalStatement(ExpressionTree finalStatement) {
        Parser parser = Parser.getInstance();
        this.finalStatement = parser.getExpressionTree("!!_A");
        ExpressionAsSchemeDecoratorInterface decorator = new ExpressionAsSchemeDecorator(this.finalStatement);
        decorator.changeVariableToExpression(new VariableName("_A"), finalStatement.getDeepCopy());
        this.finalStatement = decorator.getExpression();
        return this;
    }

    @Override
    public AbstractNewProofBuilder rebuildProof(ArrayList<ExpressionTree> statements, ExpressionTree hypA) {
        Set<ExpressionTree> deducted = new HashSet<>();
        for (ExpressionTree currentStatement : statements){
            processStatement(currentStatement, deducted);
            deducted.add(currentStatement);
        }
        return this;
    }

    private boolean isOneOfAxioms(ExpressionTree statement){
        for (ExpressionTree axiom : axioms)
            if (axiom.tryAsSchemeFor(statement))
                return true;
        return false;
    }
    private boolean isTenthAxiom(ExpressionTree currentStatement) {
        return this.tenthAxiom.tryAsSchemeFor(currentStatement);
    }
    private boolean isOneOfHypotheses(ExpressionTree statement){
        for (ExpressionTree hypothesis : hypotheses)
            if (hypothesis.equals(statement)) return true;
        return false;
    }
    private void processStatement(ExpressionTree currentStatement, Set<ExpressionTree> deducted) {
        if (isOneOfHypotheses(currentStatement)){
            addAxiomaticProof(currentStatement);
            return;
        }
        if (isTenthAxiom(currentStatement)){
            addNotTenthAxiomProof(currentStatement);
            return;
        }
        if (isOneOfAxioms(currentStatement)){
            addAxiomaticProof(currentStatement);
        }

        processAsModusPonens(currentStatement, deducted);
    }

    private void processAsModusPonens(ExpressionTree currentStatement, Set<ExpressionTree> deducted) {
        ExpressionAsSchemeDecoratorInterface decorator;
        for (ExpressionTree candidate : deducted) {
            decorator =
                    new ExpressionAsSchemeDecorator(Parser.getInstance().getExpressionTree("left -> current"));
            decorator.changeVariableToExpression(new VariableName("left"), candidate);
            decorator.changeVariableToExpression(new VariableName("current"), currentStatement);
            if (deducted.contains(decorator.getExpression())) {
                addModusPonensProof(candidate, currentStatement);
                return;
            }
        }
    }
    private void addStatementChangedVariable(String exprString, VariableName name, ExpressionTree inExpr){
        ExpressionAsSchemeDecoratorInterface decorator =
                new ExpressionAsSchemeDecorator(Parser.getInstance().getExpressionTree(exprString));
        decorator.changeVariableToExpression(name, inExpr);
        this.statements.add(decorator.getExpression());
    }

    private void addAxiomaticProof(ExpressionTree currentStatement){
        VariableName a = new VariableName("A");

        addStatementChangedVariable("A", a, currentStatement);
        addStatementChangedVariable("(A -> (!A -> A))", a, currentStatement);
        addStatementChangedVariable("(!A -> A)", a, currentStatement);
        addStatementChangedVariable("(!A -> (!A -> !A))", a, currentStatement);
        addStatementChangedVariable("((!A -> (!A -> !A)) -> ((!A -> ((!A -> !A) -> !A)) -> (!A -> !A)))", a, currentStatement);
        addStatementChangedVariable("((!A -> ((!A -> !A) -> !A)) -> (!A -> !A))", a, currentStatement);
        addStatementChangedVariable("(!A -> ((!A -> !A) -> !A))", a, currentStatement);
        addStatementChangedVariable("(!A -> !A)", a, currentStatement);
        addStatementChangedVariable("((!A -> A) -> ((!A -> !A) -> !!A))", a, currentStatement);
        addStatementChangedVariable("((!A -> !A) -> !!A)", a, currentStatement);
        addStatementChangedVariable("!!A", a, currentStatement);
    }
    private void addNotTenthAxiomProof(ExpressionTree currentStatement){
        VariableName a = new VariableName("a");

        addStatementChangedVariable("a", a, currentStatement);//0
        addStatementChangedVariable("a -> !!a -> a", a, currentStatement);
        addStatementChangedVariable("(a -> !!a -> a) -> !(!!a->a) -> (a -> !!a -> a)", a, currentStatement);
        addStatementChangedVariable("!(!!a->a) -> (a -> !!a -> a)", a, currentStatement);//3
        addStatementChangedVariable("!(!!a->a) -> a -> !(!!a->a)", a, currentStatement);
        addStatementChangedVariable("!(!!a->a) -> !(!!a->a) -> !(!!a->a)", a, currentStatement);
        addStatementChangedVariable("!(!!a->a) -> ( !(!!a->a)->!(!!a->a) ) -> !(!!a->a)", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> !(!!a->a)->!(!!a->a) ) -> (!(!!a->a) -> ( !(!!a->a)->!(!!a->a) ) -> !(!!a->a)) -> (!(!!a->a) -> !(!!a->a))", a, currentStatement);//7
        addStatementChangedVariable("(!(!!a->a) -> ( !(!!a->a)->!(!!a->a) ) -> !(!!a->a)) -> (!(!!a->a) -> !(!!a->a))", a, currentStatement);
        addStatementChangedVariable("!(!!a->a) -> !(!!a->a)", a, currentStatement);
        addStatementChangedVariable("(a -> (!!a->a)) -> (a -> !(!!a->a)) -> !a", a, currentStatement);
        addStatementChangedVariable("( (a -> (!!a->a)) -> (a -> !(!!a->a)) -> !a ) -> !(!!a->a) -> ( (a -> (!!a->a)) -> (a -> !(!!a->a)) -> !a )", a, currentStatement);//11
        addStatementChangedVariable("!(!!a->a) -> ( (a -> (!!a->a)) -> (a -> !(!!a->a)) -> !a )", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> (a -> !!a -> a)) -> (!(!!a->a) -> (a -> !!a -> a) -> ((a->!(!!a->a))->!a) ) -> (!(!!a->a) -> ((a->!(!!a->a))->!a)) )", a, currentStatement); //13
        addStatementChangedVariable("(!(!!a->a) -> (a -> !!a -> a) -> ((a->!(!!a->a))->!a) ) -> (!(!!a->a) -> ((a->!(!!a->a))->!a)) )", a, currentStatement);
        addStatementChangedVariable("!(!!a->a) -> ((a->!(!!a->a))->!a)", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> (a->!(!!a->a))) -> (!(!!a->a) -> (a->!(!!a->a))->!a) -> (!(!!a->a)->!a)", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> (a->!(!!a->a))->!a) -> (!(!!a->a)->!a)", a, currentStatement);//17
        addStatementChangedVariable("!(!!a->a)->!a", a, currentStatement);
        addStatementChangedVariable("!a -> !!a -> a", a, currentStatement);
        addStatementChangedVariable("(!a -> !!a -> a) -> !(!!a->a) -> (!a -> !!a -> a)", a, currentStatement);//20
        addStatementChangedVariable("!(!!a->a) -> (!a -> !!a -> a)", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> !a) -> (!(!!a->a) -> !a -> (!!a->a)) -> (!(!!a->a) -> (!!a->a))", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> !a -> (!!a->a)) -> (!(!!a->a) -> (!!a->a))", a, currentStatement);//23
        addStatementChangedVariable("(!(!!a->a) -> (!!a->a))", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> (!!a->a)) -> (!(!!a->a) -> !(!!a->a)) -> !!(!!a->a)", a, currentStatement);
        addStatementChangedVariable("(!(!!a->a) -> !(!!a->a)) -> !!(!!a->a)", a, currentStatement);
        addStatementChangedVariable("!!(!!a->a)", a, currentStatement);//27
    }
    private void addModusPonensProof(ExpressionTree left, ExpressionTree right){

    }

    @Override
    public Proof build() {
        return new Proof(this.hypotheses, this.axioms, this.finalStatement, this.statements);
    }
}
