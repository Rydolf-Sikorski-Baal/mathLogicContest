import Parcer.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TaskA {
    private static TaskA TaskAInstance = null;
    private TaskA(){}
    public static TaskA getInstance(){
        if (TaskAInstance == null) TaskAInstance = new TaskA();

        return TaskAInstance;
    }

    public static void main(String[] args) throws IOException {
        Parser parser = Parser.getInstance();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();

        String result = parser.getExpressionTree(input).toPrefixString();

        System.out.print(result);
    }
}
