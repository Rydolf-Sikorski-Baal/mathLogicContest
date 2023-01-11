package Expression;

import java.util.Map;

public class SettedVariablesMap {
    private final Map<VariableName, SettedVariable> variableMap;

    public SettedVariablesMap(Map<VariableName, SettedVariable> variableMap) {
        this.variableMap = variableMap;
    }

    public Map<VariableName, SettedVariable> variableMap(){return variableMap;}
}
