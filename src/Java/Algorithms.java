

import java.util.*;

public final class Algorithms {
    private Algorithms() {}

    private static Map<String, Object> result(Object... pairs) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put((String) pairs[i], pairs[i + 1]);
        }
        return m;
    }

    // ==========================
    // 1. DSU
    // ==========================

    private static final class DSU {
        final int[] parent;
        final int[] size;
        int components;
        int largest;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            components = n;
            largest = n == 0 ? 0 : 1;

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (x != parent[x]) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            a = find(a);
            b = find(b);

            if (a == b) {
                return false;
            }

            if (size[a] < size[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            parent[b] = a;
            size[a] += size[b];
            components--;
            largest = Math.max(largest, size[a]);

            return true;
        }
    }

    static Map<String, Object> dsu(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] operations = Input.operations(input);

        DSU d = new DSU(n);
        List<List<Integer>> states = new ArrayList<>();

        for (int[] op : operations) {
            d.union(op[0], op[1]);
            states.add(Arrays.asList(d.components, d.largest));
        }

        return result("states", states);
    }

    // ==========================
    // 2. KRUSKAL
    // ==========================

    static Map<String, Object> kruskal(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        Integer[] ids = new Integer[edges.length];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }

        Arrays.sort(ids, (a, b) -> {
            int comparison = Integer.compare(edges[a][2], edges[b][2]);
            if (comparison != 0) {
                return comparison;
            }
            return Integer.compare(a, b);
        });

        DSU d = new DSU(n);
        long cost = 0;
        List<Integer> chosen = new ArrayList<>();

        for (int id : ids) {
            int[] e = edges[id];

            if (d.union(e[0], e[1])) {
                cost += e[2];
                chosen.add(id);
            }
        }

        return result(
                "cost", cost,
                "edge_ids", chosen,
                "components", d.components
        );
    }

    // ==========================
    // 3. PRIM
    // ==========================

    static Map<String, Object> prim(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<List<Integer>> adj = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int i = 0; i < edges.length; i++) {
            adj.get(edges[i][0]).add(i);
            adj.get(edges[i][1]).add(i);
        }

        boolean[] seen = new boolean[n];

        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> {
            int c = Long.compare(a[0], b[0]);
            return c != 0 ? c : Long.compare(a[1], b[1]);
        });

        long cost = 0;
        int components = 0;
        List<Integer> chosen = new ArrayList<>();

        for (int start = 0; start < n; start++) {
            if (seen[start]) {
                continue;
            }

            components++;
            seen[start] = true;

            for (int id : adj.get(start)) {
                pq.add(new long[]{edges[id][2], id});
            }

            while (!pq.isEmpty()) {
                long[] current = pq.poll();
                int id = (int) current[1];

                int u = edges[id][0];
                int v = edges[id][1];

                if (seen[u] && seen[v]) {
                    continue;
                }

                int next = seen[u] ? v : u;

                if (seen[next]) {
                    continue;
                }

                seen[next] = true;
                cost += edges[id][2];
                chosen.add(id);

                for (int other : adj.get(next)) {
                    pq.add(new long[]{edges[other][2], other});
                }
            }
        }

        return result(
                "cost", cost,
                "edge_ids", chosen,
                "components", components
        );
    }

    // ==========================
    // 4. SCC - KOSARAJU
    // ==========================

    static Map<String, Object> scc(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<List<Integer>> g = new ArrayList<>(n);
        List<List<Integer>> rev = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            g.add(new ArrayList<>());
            rev.add(new ArrayList<>());
        }

        for (int[] e : edges) {
            g.get(e[0]).add(e[1]);
            rev.get(e[1]).add(e[0]);
        }

        boolean[] seen = new boolean[n];
        List<Integer> order = new ArrayList<>(n);

        for (int start = 0; start < n; start++) {
            if (seen[start]) {
                continue;
            }

            seen[start] = true;

            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[]{start, 0});

            while (!stack.isEmpty()) {
                int[] top = stack.peek();
                int v = top[0];

                if (top[1] == g.get(v).size()) {
                    order.add(v);
                    stack.pop();
                } else {
                    int w = g.get(v).get(top[1]++);

                    if (!seen[w]) {
                        seen[w] = true;
                        stack.push(new int[]{w, 0});
                    }
                }
            }
        }

        int[] component = new int[n];
        Arrays.fill(component, -1);

        int count = 0;

        for (int i = order.size() - 1; i >= 0; i--) {
            int start = order.get(i);

            if (component[start] != -1) {
                continue;
            }

            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            component[start] = count;

            while (!stack.isEmpty()) {
                int v = stack.pop();

                for (int w : rev.get(v)) {
                    if (component[w] == -1) {
                        component[w] = count;
                        stack.push(w);
                    }
                }
            }

            count++;
        }

        TreeSet<Long> unique = new TreeSet<>();

        for (int[] e : edges) {
            int a = component[e[0]];
            int b = component[e[1]];

            if (a != b) {
                long key = ((long) a << 32) | (b & 0xffffffffL);
                unique.add(key);
            }
        }

        List<List<Integer>> condensation = new ArrayList<>();

        for (long key : unique) {
            int a = (int) (key >>> 32);
            int b = (int) key;
            condensation.add(Arrays.asList(a, b));
        }

        return result(
                "component", component,
                "condensation", condensation
        );
    }

    // ==========================
    // TOPOLOGICAL SORT HELPER
    // ==========================

    private static int[] topoOrder(int n, int[][] edges) {
        List<List<Integer>> g = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            g.add(new ArrayList<>());
        }

        for (int[] e : edges) {
            g.get(e[0]).add(e[1]);
        }

        byte[] color = new byte[n];
        int[] order = new int[n];
        int pos = n - 1;

        for (int start = 0; start < n; start++) {
            if (color[start] != 0) {
                continue;
            }

            color[start] = 1;

            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[]{start, 0});

            while (!stack.isEmpty()) {
                int[] top = stack.peek();
                int v = top[0];

                if (top[1] == g.get(v).size()) {
                    color[v] = 2;
                    order[pos--] = v;
                    stack.pop();
                } else {
                    int w = g.get(v).get(top[1]++);

                    if (color[w] == 1) {
                        return null;
                    }

                    if (color[w] == 0) {
                        color[w] = 1;
                        stack.push(new int[]{w, 0});
                    }
                }
            }
        }

        return order;
    }

    // ==========================
    // 5. TOPOLOGICAL SORT
    // ==========================

    static Map<String, Object> topo(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        int[] order = topoOrder(n, edges);

        if (order == null) {
            return result(
                    "acyclic", false,
                    "order", new int[0]
            );
        }

        return result(
                "acyclic", true,
                "order", order
        );
    }

    // ==========================
    // 6. DAG SHORTEST PATH
    // ==========================

    static Map<String, Object> dag(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int source = Input.source(input);
        int[][] edges = Input.edges(input);

        int[] order = topoOrder(n, edges);

        if (order == null) {
            return result(
                    "acyclic", false,
                    "distance", new Object[0],
                    "parent_edge", new int[0]
            );
        }

        List<List<Integer>> adj = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int i = 0; i < edges.length; i++) {
            adj.get(edges[i][0]).add(i);
        }

        Long[] dist = new Long[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        dist[source] = 0L;

        for (int v : order) {
            if (dist[v] == null) {
                continue;
            }

            for (int id : adj.get(v)) {
                int w = edges[id][1];
                long candidate = dist[v] + edges[id][2];

                if (dist[w] == null || candidate < dist[w]) {
                    dist[w] = candidate;
                    parent[w] = id;
                }
            }
        }

        return result(
                "acyclic", true,
                "distance", dist,
                "parent_edge", parent
        );
    }

    // ==========================
    // ROUTE RECONSTRUCTION HELPER
    // ==========================

    static List<Integer> reconstructRoute(
            int source,
            int target,
            int[] parentEdge,
            int[][] edges
    ) {
        List<Integer> route = new ArrayList<>();

        if (target == source) {
            return Collections.singletonList(source);
        }

        boolean[] seen = new boolean[parentEdge.length];
        int v = target;

        while (v != source) {
            if (v < 0 || v >= parentEdge.length || seen[v]) {
                return Collections.emptyList();
            }

            seen[v] = true;

            int id = parentEdge[v];

            if (id < 0 || id >= edges.length || edges[id][1] != v) {
                return Collections.emptyList();
            }

            route.add(v);
            v = edges[id][0];
        }

        route.add(source);
        Collections.reverse(route);

        return route;
    }
}
