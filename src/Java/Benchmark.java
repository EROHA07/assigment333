import java.util.*;
public class Benchmark {
    static int[][] makeEdges(int n, int m, long seed) {
        Random random = new Random(seed);
        int[][] edges = new int[m][3];
        for (int i = 0; i < m; i++) {
            if (i < n - 1) { edges[i][0] = i; edges[i][1] = i + 1; }
            else { edges[i][0] = random.nextInt(n); edges[i][1] = random.nextInt(n); }
            edges[i][2] = random.nextInt(2001) - 1000;
        }
        return edges;
    }
    static double median(double[] a) { double[] b = a.clone(); Arrays.sort(b); return b[b.length/2]; }
    public static void main(String[] args) {
        int[][] sizes = {{1000,3000},{5000,15000},{10000,30000}};
        for (int[] sz : sizes) {
            int n = sz[0], m = sz[1];
            Map<String,Object> input = new HashMap<>();
            input.put("n", n);
            input.put("edges", makeEdges(n,m,254154L));
            for (int j=0;j<2;j++) { Algorithms.kruskal(input); Algorithms.prim(input); }
            double[] k = new double[3], p = new double[3];
            for (int j=0;j<3;j++) {
                long t = System.nanoTime();
                Map<String,Object> rk = Algorithms.kruskal(input);
                k[j] = (System.nanoTime()-t)/1e6;
                t = System.nanoTime();
                Map<String,Object> rp = Algorithms.prim(input);
                p[j] = (System.nanoTime()-t)/1e6;
                if (!Objects.equals(rk.get("cost"),rp.get("cost")) || !Objects.equals(rk.get("components"), rp.get("components")))
                    throw new AssertionError("MST mismatch");
            }
            System.out.printf(Locale.US,"n=%d m=%d Kruskal=%s median=%.3fms Prim=%s median=%.3fms%n", n,m,Arrays.toString(k),median(k),Arrays.toString(p),median(p));
        }
    }
}
