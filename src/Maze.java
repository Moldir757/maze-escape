/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.count()];
        int count = 0;

        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];

            if (dsu.union(a, b)) {
                broken[count] = k;
                count++;
            }
        }

        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int i = 0; i < w.count(); i++) {
            order[i] = i;
        }

        java.util.Arrays.sort(order,
                (i, j) -> Integer.compare(w.cost()[i], w.cost()[j]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] result = new int[w.cells() - 1];
        int count = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                result[count++] = k;

                if (count == w.cells() - 1) {
                    break;
                }
            }
        }

        return java.util.Arrays.copyOf(result, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();

        java.util.List<int[]>[] graph = new java.util.ArrayList[n];

        for (int i = 0; i < n; i++) {
            graph[i] = new java.util.ArrayList<>();
        }

        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];
            int cost = w.cost()[k];

            graph[a].add(new int[]{b, cost});
            graph[b].add(new int[]{a, cost});
        }

        boolean[] visited = new boolean[n];
        java.util.PriorityQueue<int[]> pq =
                new java.util.PriorityQueue<>(
                        java.util.Comparator.comparingInt(edge -> edge[1]));

        pq.add(new int[]{0, 0});
        long totalCost = 0;
        int visitedCount = 0;

        while (!pq.isEmpty() && visitedCount < n) {
            int[] edge = pq.poll();
            int cell = edge[0];
            int cost = edge[1];

            if (visited[cell]) {
                continue;
            }

            visited[cell] = true;
            visitedCount++;
            totalCost += cost;

            for (int[] next : graph[cell]) {
                if (!visited[next[0]]) {
                    pq.add(new int[]{next[0], next[1]});
                }
            }
        }

        return visitedCount == n ? totalCost : -1;
    }
}
