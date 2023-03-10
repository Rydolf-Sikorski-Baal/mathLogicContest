package Expression;

public class VariableName {
    private final String name;

    public VariableName(String name) {
        this.name = name;
    }

    public String name(){return name;}
    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
        VariableName objName = (VariableName) obj;
        return this.name.equals(objName.name());
    }

    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }
}
