package MathOperations;

public class LogicXor extends BinaryLogicOperation{
    @Override
    public boolean doOperation(boolean first, boolean second) {
        return first != second;
    }

    @Override
    public String toString(){
        return "^";
    }
}
