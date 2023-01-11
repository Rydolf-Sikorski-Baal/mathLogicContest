package MathOperations;

public class LogicInversion extends UnaryLogicOperation{
    @Override
    public boolean doOperation(boolean bool) {
        return !bool;
    }

    @Override
    public String toString(){
        return "!";
    }
}
