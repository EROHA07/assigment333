# Own tests (Java)

Run from the repository root:

```powershell
py -3 tools/run.py --setup
py -3 tools/run.py --check
py -3 tools/run.py --input tests/my_cases.jsonl
```

The last command outputs **actual** results, not an automatic verdict. Save that output and compare against the justified expectations below. Do not claim cases are tested until the command has actually run.

| Case | Expected property and reason |
|---|---|
| 1 ping | `status=ready` from adapter |
| 2 DSU | states `[[3,2],[2,3],[2,3],[2,3]]`: first two unions join components; duplicate/self unions do not |
| 3 empty DSU | empty states |
| 4 Kruskal | cost 5, edge IDs {1,2}, 2 components including isolate 3 |
| 5 Prim | cost 5, edge IDs {1,2}, 2 components |
| 6 SCC | 0 and 1 same component; 2 and 3 distinct; condensation follows `{0,1} -> {2} -> {3}` with two distinct pairs |
| 7 topo | acyclic=true; all vertices included; edges 0->2,1->2,2->3 are forward |
| 8 topo cycle | acyclic=false and empty order |
| 9 DAG | distances `[0,3,-4,0,null]`, parent edges `[-1,0,2,3,-1]`; reachable route 0,1,2,3 |
| 10 unreachable DAG cycle | acyclic=false, both arrays empty: must detect cycle in all components |
| 11 parallel DAG | distances `[0,1,null]`; parent edge of vertex 1 is either 0 or 1 |
| 12 Kruskal negatives | cost -2000000000, 1 component, IDs {0,1} |
| 13 Prim negatives | cost -2000000000, 1 component, IDs {0,1} |

## Semantic validation (non-unique witnesses)

* Example alternative **valid** topo output for case 7: `[1,0,2,3]`. `[0,1,2,3]` is also valid.
* Example **invalid** topo witness: `[2,0,1,3]`, because edge 0->2 runs backward.
* Case 4/5 forest witness: `{1,2}` connects 0,1,2 without cycle and isolates 3; cost=2+3=5.
* Invalid forest witness `{0,1}` costs 6, not optimum.
* SCC labels may be arbitrary but must preserve identical membership and exact distinct condensation pairs.
* A DAG predecessor path must reach source, use original input edge IDs, and attain reported distance.

## More tests to perform before claiming full requirements

Generate 100000-vertex chains and up to 250000 edges, perform repeated mixed JSON Lines requests in one process, include large weights where a cumulative total exceeds 32-bit integers, and benchmark Kruskal and Prim at three sizes. Record actual results and run environment rather than extrapolating from small samples.
