import Parcer.ProofParser;
import Expression.ExpressionTree;
import proofs.Proof;
import proofs.ProofBuilderDirector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaskC {
    private static TaskC TaskCInstance = null;
    private TaskC(){}
    public static TaskC getInstance(){
        if (TaskCInstance == null) TaskCInstance = new TaskC();

        return TaskCInstance;
    }

    public static void main(String[] args) throws IOException{
        TaskC taskC = TaskC.getInstance();

        String input = read(System.in);

        Proof proof = ProofParser.getInstance().parseProofFromString(input);

        String result = taskC.getNewProof(proof);

        System.out.print(result);
    }

    public static String read(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null){
            if (line.isEmpty()) break;
            if (isNotUseless(line)) builder.append(line).append('\n');
        }

        return builder.toString();
    }

    private static boolean isEmpty(char ch){
        return (ch == ' ') || (ch == '\n') || (ch == '\0') || (ch == '\u0009') || (ch == '\r');
    }
    private static boolean isNotUseless(String line) {
        for (char ch : line.toCharArray())
            if (!isEmpty(ch)) return true;
        return false;
    }

    public String getNewProof(Proof oldProof){
        ExpressionTree hypA = oldProof.hypotheses().get(oldProof.hypotheses().size() - 1);
        Proof newProof = ProofBuilderDirector.getInstance().buildProof(oldProof, hypA);
        return newProof.toString();
    }
}
