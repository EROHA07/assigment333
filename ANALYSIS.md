# Assignment 3 — Algorithm Analysis

Student: Nassypkeliyev Yerassyl  
Language: Java 17  
Input: UTF-8 JSON Lines, one independent request per line. `Main` and `Input` are the unchanged supplied adapters.

## DSU

**Steps:** Create one singleton set per vertex. `find` compresses paths by halving; `union` attaches the smaller tree below the larger one. After each operation output component count and largest size.

**Invariant:** Every vertex points through parents to exactly one representative, whose `size` equals the number of vertices in its set; `components` counts the representatives.

**Correctness:** A union of different roots merges exactly the two represented components, updating their total size and decreasing `components` once. An operation on the same root changes nothing. Path compression preserves roots and connectivity.

**Boundaries:** Empty graph, repeated pairs and self-unions.

**Complexity:** `O(n + q α(n))` amortized time; `O(n + q)` space including recorded states.

## Kruskal

**Steps:** Sort original edge IDs by nondecreasing weight; use DSU to select only edges joining distinct sets. Sum weights into `long`.

**Invariant:** Chosen edges are acyclic; every chosen edge joins two previously disconnected forest trees.

**Correctness:** The cut property makes every chosen minimum admissible edge safe. Once all edges are processed, the forest spans each input component with minimum weight.

**Boundaries:** Isolates, loops (rejected by DSU), parallel edges, negative/equal weights, empty graph.

**Complexity:** `O(n + m log(m+1))` time, `O(n + m)` space.

## Prim

**Steps:** Build undirected adjacency lists indexed by original edge ID. For each unseen vertex, start a new component and grow a tree using the minimum-weight edge in a priority queue that crosses to an unseen vertex.

**Invariant:** Seen vertices of each discovered component are joined by chosen edges, and the heap contains incident candidate edges; selected edges never form a cycle.

**Correctness:** The lowest-weight crossing edge is safe by the cut property. Restarting at every unseen vertex covers disconnected components and isolates.

**Boundaries:** Loops are skipped, equal/parallel/negative edges supported; heap is lazy.

**Complexity:** `O(n + m log(m+1))` time; `O(n+m)` space.

## SCC — Kosaraju

**Steps:** Build graph and its transpose. Use an explicit DFS stack to collect vertices in finish order. Process vertices in decreasing finish order on the transpose, assigning one label per reached SCC. Deduplicate cross-label edges for condensation.

**Invariant:** In pass two, each unlabeled traversal covers precisely one SCC. Each distinct original edge crossing labels appears once in condensation.

**Correctness:** The DFS finish-time ordering and transpose ensure the second traversal cannot absorb a distinct unprocessed SCC. Each vertex is assigned once; scanning all input edges produces the exact condensation.

**Boundaries:** Isolates, loops, duplicate edges, empty graph, very deep chains.

**Complexity:** `O(n+m)` expected time with hash deduplication (this implementation uses ordered `TreeSet`, so `O(n+m log(m+1))` worst-case for condensation); `O(n+m)` space.

## Topological sort

**Steps:** Use an explicit DFS stack, retaining the next neighbor index, and three colors: 0 unseen, 1 active, 2 completed. A 1-colored edge endpoint proves a directed cycle. Otherwise reverse finish order.

**Invariant:** Completed vertices have all their outgoing neighbors fully processed. The stack models recursive DFS with correct suspension points.

**Correctness:** A back edge to an active vertex forms a cycle. If no back edge exists, finishing vertices after their descendants and reversing the order puts every directed edge forward.

**Boundaries:** Self-loops, unreachable subgraphs, repeated edges, empty graph, deep chains.

**Complexity:** `O(n+m)` time and space.

## DAG shortest paths

**Steps:** Run whole-graph topological DFS first; if cyclic return `acyclic=false` and empty arrays. Otherwise initialize source distance 0 and other distances null; relax edges once in topological order, only from reachable vertices. Store original edge IDs in `parent_edge`.

**Invariant:** Immediately before processing vertex v, its distance is the shortest source-to-v path length, because all its predecessors occur earlier in a topological order.

**Correctness:** The topological invariant and relaxation establish shortest distances even when weights are negative. Each selected parent edge is tight and points to a predecessor, so following parent edges reaches the source.

**Boundaries:** Cycles even outside source reachability, isolates, parallel/tied and negative edges, unreachable nulls, 64-bit distance sums.

**Complexity:** `O(n+m)` time/space; route reconstruction `O(L)` time/output for L vertices.

## Route-reconstruction helper

Method: `Algorithms.reconstructRoute(int source, int target, int[] parentEdge, int[][] edges)`. It returns vertex IDs including endpoints, `[source]` for source, or `[]` when no valid parent chain reaches source. It verifies parent edge direction and guards against loops.

Example graph (edge IDs 0..3): `[[0,1,3],[0,2,8],[1,2,-7],[2,3,4]]`, source 0, parents `[-1,0,2,3,-1]`.

* `reconstructRoute(0,3,parents,edges)` -> `[0,1,2,3]`, sum `3 + (-7) + 4 = 0`.
* `reconstructRoute(0,0,parents,edges)` -> `[0]`, cost 0.
* `reconstructRoute(0,4,parents,edges)` -> `[]` (unreachable).
* For tied parallel edges `[[0,1,1],[0,1,1]]` and parents `[-1,0,-1]`, route to 1 is `[0,1]`, edge ID 0, cost 1; selecting edge ID 1 instead yields the same valid route and weight.

The examples above describe reproducible expected helper calls. To provide **executed** evidence, run an auxiliary Java harness calling this helper and paste its console output here.

## State reset and stack safety

All graph arrays, queues and DSU instances are local to each task call, so consecutive JSON Lines requests have independent state. DFS uses explicit stacks instead of recursive calls to avoid Java call-stack overflow on deep chains.

## Reproducible commands

From repository root, with JDK 17+ and Python 3.12+ installed:

```powershell
py -3 tools/run.py --setup
py -3 tools/run.py --check
py -3 tools/run.py --input tests/my_cases.jsonl
```

## Kruskal vs Prim benchmark — REQUIRED TO RUN

Benchmark both methods on the **same generated graph** at each size, such as `(n,m)=(1000,3000),(5000,15000),(10000,30000)`, with fixed random seed `254154`. Include connected backbones and random extra edges. Confirm the optimum cost and forest validity before timing. Warm up both methods. Use `System.nanoTime()` around method invocation alone, excluding compilation, process launch, JSON parsing and serialization; repeat at least 3 measured times per method at each size. Record machine CPU, RAM, OS, JDK version, seed, actual timings, medians and any discrepancies below. Do not treat the following as measurements:

| n | m | Kruskal trial 1 / 2 / 3 (ms) | Median (ms) | Prim trial 1 / 2 / 3 (ms) | Median (ms) |
|---|---|---|---|---|---|
| 1000 | 3000 | NOT MEASURED | — | NOT MEASURED | — |
| 5000 | 15000 | NOT MEASURED | — | NOT MEASURED | — |
| 10000 | 30000 | NOT MEASURED | — | NOT MEASURED | — |

## External sources and AI disclosure

Source: official Assignment 3 README and starter project. ChatGPT assisted with implementing Java algorithms and drafting this explanation and test plan. Independently run the code/tests, check output witnesses and replace any example-only claims with recorded evidence before submission. No benchmark values have been invented.
