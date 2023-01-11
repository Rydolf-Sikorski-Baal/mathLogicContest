package MathOperations;

public class LogicMultiplication extends BinaryLogicOperation{
    @Override
    public boolean doOperation(boolean first, boolean second) {
        return first && second;
    }

    @Override
    public String toString(){
        return "&";
    }
}
