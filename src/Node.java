import java.util.Collections;
import java.util.List;

public class Node {
    public final String name;
    public int num;
    public final Node left;
    public Node right;

    public Node(String name, Node l, Node r, int num) {
        this.name = name;
        this.num = num;
        left = l;
        right = r;
    }

    public void visualize(int level) {
        List<String> vals = Collections.nCopies(level, ".");
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", vals));
        if(vals.size()>0){
            sb.append(" ");
        }
        sb.append(name);
        sb.append("("+num+")");
        System.out.println(sb.toString());
    }
}
