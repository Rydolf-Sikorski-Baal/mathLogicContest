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
        return this.name.equals(((VariableName) obj).name());
    }

    @Override
    public String toString(){
        if (this.name.charAt(0) == '_') return this.name.substring(1);
        return this.name;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }
}
