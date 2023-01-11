package MathOperations;

public abstract class LogicOperation {
    @Override
    public boolean equals(Object obj){
        return this.getClass() == obj.getClass();
    }
}
