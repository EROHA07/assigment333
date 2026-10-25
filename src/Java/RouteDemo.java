import java.util.*;
public class RouteDemo {
    public static void main(String[] args) {
        int[][] edges = {{0,1,3},{0,2,8},{1,2,-7},{2,3,4}};
        int[] parent = {-1,0,2,3,-1};
        System.out.println("reachable="+Algorithms.reconstructRoute(0,3,parent,edges));
        System.out.println("source="+Algorithms.reconstructRoute(0,0,parent,edges));
        System.out.println("unreachable="+Algorithms.reconstructRoute(0,4,parent,edges));
        int[][] tied = {{0,1,1},{0,1,1}};
        System.out.println("parallel="+Algorithms.reconstructRoute(0,1,new int[]{-1,1},tied));
    }
}
