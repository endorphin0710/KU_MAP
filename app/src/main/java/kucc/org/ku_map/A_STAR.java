package kucc.org.ku_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class A_STAR {

    private List<Integer> path;

    public List<Integer> printPath(Node target) {

        path = new ArrayList<>();

        Node node = target;
        while(node != null){
            path.add(node.index);
            node = node.parent;
        }

        Collections.reverse(path);

        return path;
    }

    public void search(Node source, Node destination) {

        /** Closed list for A* algorithm **/
        Set<Node> closedList = new HashSet<Node>();

        /** Open list which is implemented as priority queue (in ascending order) **/
        PriorityQueue<Node> openList = new PriorityQueue<Node>(20, new Comparator<Node>() {
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
        source.f_scores = source.g_scores + source.h_scores;
        openList.add(source);

        /** destination not found yet **/
        boolean found = false;

        while (!found) {

            /** remove a node with minimum f, which is g+h, in the open list and add it to the closed list **/
            Node current_node = openList.poll();
            closedList.add(current_node);

            /** add adjacencies of current node to the open list if it is not in the closed list**/
            for(Edge e : current_node.adjacencies){

                Node child = e.target;
                double cost = e.cost;
                double temp_g_score = current_node.g_scores + cost;
                double temp_f_score = temp_g_score + child.h_scores;

                /** Add child node to the open list if closed list does not contain child node
                 *  OR replace information if current f is lower than original f value
                 */
                if(!closedList.contains(child) || temp_f_score < child.f_scores){

                    child.parent = current_node;
                    child.g_scores = temp_g_score;
                    child.f_scores = temp_f_score;

                    if(openList.contains(child)) {
                        openList.remove(child);
                    }
                    openList.add(child);

                    if(child == destination){
                        found = true;
                    }
                }
            }
        }
    }
}

class Node {

    public final int index;
    public final String value;
    public double g_scores;
    public double h_scores;
    public double f_scores = 0;
    public Edge[] adjacencies;
    public Node parent;

    public Node(int index, String val, double hVal) {
        this.index = index;
        this.value = val;
        this.h_scores = hVal;
        this.parent = null;
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