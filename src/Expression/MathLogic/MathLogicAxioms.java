package Expression.MathLogic;

import Expression.ExpressionTree;
import Parcer.Parser;
import javafx.beans.binding.ListExpression;

import java.util.ArrayList;
import java.util.List;

public class MathLogicAxioms {
    private static List<ExpressionTree> classicAxiomsList;
    static{
        classicAxiomsList = new ArrayList<>();
        Parser parser = Parser.getInstance();
        classicAxiomsList.add(parser.getExpressionTree("a -> (b -> a)"));
        classicAxiomsList.add(parser.getExpressionTree("(a -> b) -> (a -> b -> c) -> (a -> c)"));
        classicAxiomsList.add(parser.getExpressionTree("a -> (b -> a & b)"));
        classicAxiomsList.add(parser.getExpressionTree("a & b -> a"));
        classicAxiomsList.add(parser.getExpressionTree("a & b -> b"));

        classicAxiomsList.add(parser.getExpressionTree("a -> (a | b)"));
        classicAxiomsList.add(parser.getExpressionTree("b -> (a | b)"));
        classicAxiomsList.add(parser.getExpressionTree("(a -> c) -> (b -> c) -> ((a | b) -> c)"));
        classicAxiomsList.add(parser.getExpressionTree("(a -> b) -> (a -> !b) -> !a"));
        classicAxiomsList.add(parser.getExpressionTree("!!a->a"));
    }

    private static List<ExpressionTree> intuitionistAxiomsList;
    static{
        intuitionistAxiomsList = new ArrayList<>();
        Parser parser = Parser.getInstance();
        intuitionistAxiomsList.add(parser.getExpressionTree("a -> (b -> a)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("(a -> b) -> (a -> b -> c) -> (a -> c)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("a -> (b -> a & b)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("a & b -> a"));
        intuitionistAxiomsList.add(parser.getExpressionTree("a & b -> b"));

        intuitionistAxiomsList.add(parser.getExpressionTree("a -> (a | b)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("b -> (a | b)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("(a -> c) -> (b -> c) -> ((a | b) -> c)"));
        intuitionistAxiomsList.add(parser.getExpressionTree("(a -> b) -> (a -> !b) -> !a"));
        intuitionistAxiomsList.add(parser.getExpressionTree("a -> (!a -> b)"));
    }

    public static List<ExpressionTree> getClassicAxiomsList(){return classicAxiomsList;}
    public static List<ExpressionTree> getIntuitionistAxiomsList() {return intuitionistAxiomsList;}
}
