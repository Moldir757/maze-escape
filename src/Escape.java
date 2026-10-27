/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int rows = w.rows();
        int cols = w.cols();
        int n = w.cells();

        int[][] graph = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int cell = 0; cell < n; cell++) {
            if (graph[cell].length == 1) {
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        java.util.Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int[] nextDirection = new int[n];
        int top = 0;
        int visitedCount = 1;

        stack[0] = 0;
        visited[0] = true;

        int[] directions = {-cols, 1, cols, -1};
        int exit = n - 1;

        while (top >= 0 && !visited[exit]) {
            int cell = stack[top];

            if (nextDirection[top] == 4) {
                top--;
                continue;
            }

            int next = cell + directions[nextDirection[top]];
            nextDirection[top]++;

            if (next < 0 || next >= n) {
                continue;
            }

            if (nextDirection[top] == 2 && cell % cols == cols - 1) {
                continue;
            }

            if (nextDirection[top] == 4 && cell % cols == 0) {
                continue;
            }

            boolean isNeighbour = false;
            for (int neighbour : graph[cell]) {
                if (neighbour == next) {
                    isNeighbour = true;
                    break;
                }
            }

            if (!isNeighbour || visited[next]) {
                continue;
            }

            visited[next] = true;
            visitedCount++;
            parent[next] = cell;

            top++;
            stack[top] = next;
            nextDirection[top] = 0;
        }

        if (!visited[exit]) {
            return new Result(new int[0], visitedCount, deadEnds);
        }

        int length = 1;
        for (int cell = exit; cell != 0; cell = parent[cell]) {
            length++;
        }

        int[] path = new int[length];
        int cell = exit;

        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }

        return new Result(path, visitedCount, deadEnds);
    }
}
