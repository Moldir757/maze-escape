/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();

        int[][] outgoing = Graphs.slidesFrom(s);
        int[][] incoming = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] finishOrder = new int[n];
        int finishCount = 0;

        int[] stack = new int[n];
        int[] nextEdge = new int[n];

        // First pass: DFS on the original graph.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            nextEdge[0] = 0;
            visited[start] = true;

            while (top >= 0) {
                int cell = stack[top];

                if (nextEdge[top] < outgoing[cell].length) {
                    int slide = outgoing[cell][nextEdge[top]++];
                    int next = s.to()[slide];

                    if (!visited[next]) {
                        visited[next] = true;
                        top++;
                        stack[top] = next;
                        nextEdge[top] = 0;
                    }
                } else {
                    finishOrder[finishCount++] = cell;
                    top--;
                }
            }
        }

        // Second pass: DFS on the reversed graph.
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);

        int zone = 0;

        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finishOrder[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            zoneOf[start] = zone;

            while (top >= 0) {
                int cell = stack[top--];

                for (int slide : incoming[cell]) {
                    int next = s.from()[slide];

                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zone;
                        stack[++top] = next;
                    }
                }
            }

            zone++;
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();

        long[] distance = new long[zoneCount];
        java.util.Arrays.fill(distance, Long.MAX_VALUE);

        int[] stages = new int[zoneCount];

        int startZone = zoneOf[0];
        int exitZone = zoneOf[n - 1];

        distance[startZone] = 0;
        stages[startZone] = 1;

        // Process zones in topological order.
        for (int i = 0; i < s.count(); i++) {
            int fromCell = s.from()[i];
            int toCell = s.to()[i];

            int fromZone = zoneOf[fromCell];
            int toZone = zoneOf[toCell];

            if (fromZone == toZone) {
                continue;
            }

            // Keep the cheapest slide between these zones.
            // We process these edges below using the zone order.
        }

        // Dynamic programming over zones in topological order.
        for (int zone = 0; zone < zoneCount; zone++) {
            if (distance[zone] == Long.MAX_VALUE) {
                continue;
            }

            for (int i = 0; i < s.count(); i++) {
                int fromZone = zoneOf[s.from()[i]];
                int toZone = zoneOf[s.to()[i]];

                if (fromZone != zone || toZone == zone) {
                    continue;
                }

                long cost = s.time()[i];

                distance[toZone] = Math.min(
                        distance[toZone], distance[zone] + cost);

                stages[toZone] = Math.max(
                        stages[toZone], stages[zone] + 1);
            }
        }

        return new long[]{
                stages[exitZone],
                distance[exitZone] == Long.MAX_VALUE ? -1 : distance[exitZone]
        };
    }
}
