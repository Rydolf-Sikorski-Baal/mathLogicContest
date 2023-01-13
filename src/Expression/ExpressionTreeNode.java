package Expression;

import java.util.Map;

public abstract class ExpressionTreeNode {
    public abstract boolean getResult(SettedVariablesMap variablesMap);

    public abstract boolean tryAsSchemeFor(Object obj, Map<VariableName, ExpressionTreeNode> map);

    public abstract String toPrefixString();
}
