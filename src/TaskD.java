import Expression.ExpressionTree;
import Parcer.Parser;
import Parcer.ProofParser;
import proofs.Proof;
import proofs.ProofBuilderDirector;
import proofs.newIntuitionistProofBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaskD {
    private static TaskD TaskDInstance = null;
    private TaskD(){}
    public static TaskD getInstance(){
        if (TaskDInstance == null) TaskDInstance = new TaskD();

        return TaskDInstance;
    }

    public static void main(String[] args) throws IOException{
        TaskD TaskD = getInstance();

        String input = read(System.in);

        Proof proof = ProofParser.getInstance().parseProofFromString(input);

        String result = TaskD.getNewProof(proof);

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
        ProofBuilderDirector director = new ProofBuilderDirector(new newIntuitionistProofBuilder());
        Proof newProof = director.buildProof(oldProof, hypA);
        return newProof.toString();
    }
}
