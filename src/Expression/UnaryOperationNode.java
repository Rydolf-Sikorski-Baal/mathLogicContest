package Expression;

import MathOperations.UnaryLogicOperation;

import java.util.Map;

public class UnaryOperationNode extends ExpressionTreeNode{
    private ExpressionTreeNode boolNode;
    private final UnaryLogicOperation nodeOperation;

    public void setBoolNode(ExpressionTreeNode boolNode){this.boolNode = boolNode;}
    public ExpressionTreeNode getBoolNode(){return boolNode;}

    public UnaryOperationNode(UnaryLogicOperation nodeOperation) {
        this.nodeOperation = nodeOperation;
    }

    public boolean doNodeOperation(boolean bool){ return nodeOperation.doOperation(bool);}

    @Override
    public boolean getResult(SettedVariablesMap variablesMap) {
        return doNodeOperation(boolNode.getResult(variablesMap));
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
        return nodeOperation.equals(((UnaryOperationNode) obj).nodeOperation)
                && (boolNode.equals(((UnaryOperationNode) obj).getBoolNode()));
    }

    public boolean tryAsSchemeFor(Object obj, Map<VariableName, ExpressionTreeNode> map){
        if (obj.getClass() != this.getClass()) return false;
        return nodeOperation.equals(((UnaryOperationNode) obj).nodeOperation)
                && (boolNode.tryAsSchemeFor(((UnaryOperationNode) obj).getBoolNode(), map));
    }

    @Override
    public String toString(){
        return this.nodeOperation.toString()
                +
                (this.boolNode.getClass() == VariableNode.class || this.boolNode.getClass() == UnaryOperationNode.class
                        ? this.boolNode.toString() : '(' + this.boolNode.toString() + ')');
    }

    @Override
    public String toPrefixString(){
        return '(' + this.nodeOperation.toString() + this.boolNode.toPrefixString() + ')';
    }
}
