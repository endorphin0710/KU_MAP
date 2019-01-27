package kucc.org.ku_map.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Dijkstra {

    final static int INF = Integer.MAX_VALUE;
    static int[][] adj = {
            {0,INF,1,2,INF},
            {INF,0,3,INF,1},
            {1,3,0,INF,INF},
            {2,INF,INF,0,3},
            {INF,1,INF,3,0},
    };
    static boolean[] visited = new boolean[adj.length];
    static int[] d = new int[adj.length];

    public static ArrayList<Integer> DA(int start, int destination) {

        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < adj.length; i++) {
            paths.add(new ArrayList<Integer>());
        }
        Arrays.fill(d, INF);
        d[start] = 0;
        paths.get(start).add(start);
        pq.offer(new Node(start, d[start]));

        while(!pq.isEmpty()) {

            int current_index = pq.peek().getIndex();
            visited[current_index] = true;
            pq.poll();

            for(int i = 0; i < adj.length; i++) {
                if(adj[current_index][i] == INF || visited[i] == true)
                    continue;
                if(adj[current_index][i]+d[current_index] < d[i]) {
                    d[i] = adj[current_index][i]+d[current_index];
                    paths.get(i).clear();
                    for(Integer a : paths.get(current_index))
                        paths.get(i).add(a);
                    paths.get(i).add(i);
                }
                pq.offer(new Node(i, d[i]));
            }

        }

        return paths.get(destination);

    }

}

class Node implements Comparable<Node> {

    private int index;
    private int distance;

    Node(int index, int distance){
        this.index = index;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Node o) {
        return distance <= o.distance ? -1 : 1;
    }

}

