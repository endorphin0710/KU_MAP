package kucc.org.ku_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class A_STAR {

    public List<Integer> printPath(Node target) {
        List<Integer> path = new ArrayList<>();

        for (Node node = target; node != null; node = node.parent) {
            path.add(node.index);
        }

        Collections.reverse(path);

        return path;
    }

    public void search(Node source, Node dest) {

        Set<Node> explored = new HashSet<Node>();

        PriorityQueue<Node> pq = new PriorityQueue<Node>(20, new Comparator<Node>() {
            public int compare(Node i, Node j) {
                if (i.f_scores > j.f_scores) {
                    return 1;
                }
                else if (i.f_scores < j.f_scores) {
                    return -1;
                }
                else {
                    return 0;
                }
            }

        });

        source.g_scores = 0;
        pq.add(source);

        boolean found = false;

        while ((!pq.isEmpty()) && (!found)) {

            // the node in having the lowest f_score value
            Node current = pq.poll();

            explored.add(current);

            // goal found
            if (current.index == dest.index) {
                found = true;
            }

            // check every child of current node
            for (Edge e : current.adjacencies) {
                Node child = e.target;
                double cost = e.cost;
                double temp_g_scores = current.g_scores + cost;
                double temp_f_scores = temp_g_scores + child.h_scores;

                /*
                 * if child node has been evaluated and the newer f_score is higher, skip
                 */
                if ((explored.contains(child)) && (temp_f_scores >= child.f_scores)) {
                    continue;
                }

                /*
                 * else if child node is not in queue or newer f_score is lower
                 */
                else if ((!pq.contains(child)) || (temp_f_scores < child.f_scores)) {

                    child.parent = current;
                    child.g_scores = temp_g_scores;
                    child.f_scores = temp_f_scores;

                    if (pq.contains(child)) {
                        pq.remove(child);
                    }

                    pq.add(child);

                }
            }
        }
    }
}

class Node {

    public final int index;
    public final String value;
    public double g_scores;
    public final double h_scores;
    public double f_scores = 0;
    public Edge[] adjacencies;
    public Node parent;

    public Node(int index, String val, double hVal) {
        this.index = index;
        this.value = val;
        this.h_scores = hVal;
    }

}

class Edge{

    public final double cost;
    public final Node target;

    public Edge(Node targetNode, double costVal) {
        target = targetNode;
        cost = costVal;
    }
}