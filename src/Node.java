import java.util.Collections;
import java.util.List;

class Node {
    final String name;
    final Node left;
    Node right;
    private int num;

    Node(String name, Node l, Node r, int num) {
        this.name = name;
        this.num = num;
        left = l;
        right = r;
    }

    String visualize(int level) {
        List<String> vals = Collections.nCopies(level, ".");
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", vals));
        if (vals.size() > 0) {
            sb.append(" ");
        }
        sb.append(name);
        sb.append("(").append(num).append(")");
        return sb.toString();
    }
}
