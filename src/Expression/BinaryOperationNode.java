package Expression;

import MathOperations.BinaryLogicOperation;

import java.util.Map;

public class BinaryOperationNode extends ExpressionTreeNode{
    private final BinaryLogicOperation nodeOperation;
    private ExpressionTreeNode firstNode;
    private ExpressionTreeNode secondNode;

    public BinaryLogicOperation getNodeOperation(){return nodeOperation;}
    public ExpressionTreeNode getFirstNode(){return firstNode;}
    public ExpressionTreeNode getSecondNode(){return secondNode;}
    public void setFirstNode(ExpressionTreeNode firstNode){this.firstNode = firstNode;}
    public void setSecondNode(ExpressionTreeNode secondNode){this.secondNode = secondNode;}

    public BinaryOperationNode(BinaryLogicOperation nodeOperation) {
        this.nodeOperation = nodeOperation;
    }

    public boolean doNodeOperation(boolean first, boolean second){ return nodeOperation.doOperation(first, second);}

    @Override
    public boolean getResult(SettedVariablesMap variablesMap) {
        return doNodeOperation(firstNode.getResult(variablesMap), secondNode.getResult(variablesMap));
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
        return (nodeOperation.equals(((BinaryOperationNode) obj).getNodeOperation())) &&
                (firstNode.equals(((BinaryOperationNode) obj).getFirstNode())) &&
                (secondNode.equals(((BinaryOperationNode) obj).getSecondNode()));
    }

    public boolean tryAsSchemeFor(Object obj, Map<VariableName, ExpressionTreeNode> map){
        if (obj.getClass() != this.getClass()) return false;
        BinaryOperationNode objNode = (BinaryOperationNode) obj;
        return (nodeOperation.equals(objNode.getNodeOperation())) &&
                (firstNode.tryAsSchemeFor(objNode.getFirstNode(), map)) &&
                (secondNode.tryAsSchemeFor(objNode.getSecondNode(), map));
    }

    @Override
    public String toString(){
        return '(' + this.firstNode.toString() + this.nodeOperation.toString() + this.secondNode.toString() + ')';
    }

    @Override
    public String toPrefixString() {
        return
                '(' +
                        this.nodeOperation.toString() +
                        ',' + this.firstNode.toPrefixString() +
                        ',' + this.secondNode.toPrefixString() +
                        ')';
    }
}
