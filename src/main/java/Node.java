package main.java;

import java.util.Collections;
import java.util.List;

class Node {
    final String name;
    final Node left;
    Node right;
    int childrenCount;

    Node(String name, Node l, Node r, int num) {
        this.name = name;
        this.childrenCount = num;
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
        sb.append("(").append(childrenCount).append(")");
        return sb.toString();
    }

    Node getChild(int index){
        if(index == 0){
            return this.left;
        }else{
            Node n = this.left;
            for (int i = 1; i <= index; i++) {
                n = n.right;
            }
            return n;
        }
    }
}
