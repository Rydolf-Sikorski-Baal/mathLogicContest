package Expression;

public class SettedVariable {
    private final VariableName name;
    public VariableName name(){return name;}
    private final boolean value;
    public boolean value(){return value;}

    public SettedVariable(VariableName name, boolean value) {
        this.name = name;
        this.value = value;
    }
}
