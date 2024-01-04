/**
 * Utility class for finding horizontal and vertical seams. The public methods
 * depend on the energyMatrix method implemented in SeamCarver.
 * 
 * @author Chris Mayfield
 * @version 01/25/2022
 */
public class ProvidedCode {

    /**
     * Finds the next horizontal seam to remove.
     * 
     * @param sc the seam carver to use
     * @return sequence of row indices
     */
    public static int[] findHorizontalSeam(SeamCarver sc) {
        int width = sc.getWidth();
        int height = sc.getHeight();

        // original energy of each pixel
        int[][] matrix = sc.energyMatrix();

        // total energy of shortest path starting from (x,y)
        int[][] sum = new int[width][height];

        // direction (-1, 0, +1) of shortest path from (x,y)
        int[][] dir = new int[width][height];

        // use dynamic programming to compute sum and dir
        int last = width - 1;
        for (int y = 0; y < height; y++) {
            sum[last][y] = matrix[last][y];
        }
        for (int x = width - 2; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                int move = minH(sum, x, y, height);
                sum[x][y] = matrix[x][y] + sum[x + 1][y + move];
                dir[x][y] = move;
            }
        }

        // find starting index of the overall shortest path
        int idx = 0;
        int min = sum[0][0];
        for (int y = 1; y < height; y++) {
            if (sum[0][y] < min) {
                min = sum[0][y];
                idx = y;
            }
        }

        // follow the shortest path to build the seam array
        int[] seam = new int[width];
        for (int x = 0; x < width; x++) {
            seam[x] = idx;
            idx += dir[x][idx];
        }
        return seam;
    }

    /**
     * Finds the next vertical seam to remove.
     * 
     * @param sc the seam carver to use
     * @return sequence of column indices
     */
    public static int[] findVerticalSeam(SeamCarver sc) {
        int width = sc.getWidth();
        int height = sc.getHeight();

        // original energy of each pixel
        int[][] matrix = sc.energyMatrix();

        // total energy of shortest path starting from (x,y)
        int[][] sum = new int[width][height];

        // direction (-1, 0, +1) of shortest path from (x,y)
        int[][] dir = new int[width][height];

        // use dynamic programming to compute sum and dir
        int last = height - 1;
        for (int x = 0; x < width; x++) {
            sum[x][last] = matrix[x][last];
        }
        for (int y = height - 2; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int move = minV(sum, x, y, width);
                sum[x][y] = matrix[x][y] + sum[x + move][y + 1];
                dir[x][y] = move;
            }
        }

        // find starting index of the overall shortest path
        int idx = 0;
        int min = sum[0][0];
        for (int x = 1; x < width; x++) {
            if (sum[x][0] < min) {
                min = sum[x][0];
                idx = x;
            }
        }

        // follow the shortest path to build the seam array
        int[] seam = new int[height];
        for (int y = 0; y < height; y++) {
            seam[y] = idx;
            idx += dir[idx][y];
        }
        return seam;
    }

    /**
     * Determines the next horizontal move to minimize energy.
     * 
     * @param sum cumulative matrix
     * @param x column index
     * @param y row index
     * @param height the picture width
     * @return -1, 0, or +1 depending on whether to move up, ahead, or down
     */
    private static int minH(int[][] sum, int x, int y, int height) {
        int a;  // energy of (x+1, y-1)
        int b;  // energy of (x+1, y+0)
        int c;  // energy of (x+1, y+1)

        // deal with the edge cases
        if (y > 0) {
            a = sum[x + 1][y - 1];
        } else {
            a = Integer.MAX_VALUE;
        }
        b = sum[x + 1][y + 0];
        if (y < height - 1) {
            c = sum[x + 1][y + 1];
        } else {
            c = Integer.MAX_VALUE;
        }

        // find the minimum energy
        return min3(a, b, c);
    }

    /**
     * Determines the next vertical move to minimize energy.
     * 
     * @param sum cumulative matrix
     * @param x column index
     * @param y row index
     * @param width the picture width
     * @return -1, 0, or +1 depending on whether to move left, ahead, or right
     */
    private static int minV(int[][] sum, int x, int y, int width) {
        int a;  // energy of (x-1, y+1)
        int b;  // energy of (x+0, y+1)
        int c;  // energy of (x+1, y+1)

        // deal with the edge cases
        if (x > 0) {
            a = sum[x - 1][y + 1];
        } else {
            a = Integer.MAX_VALUE;
        }
        b = sum[x + 0][y + 1];
        if (x < width - 1) {
            c = sum[x + 1][y + 1];
        } else {
            c = Integer.MAX_VALUE;
        }

        // find the minimum energy
        return min3(a, b, c);
    }

    /**
     * Returns the offset of the minimum energy.
     * 
     * @param a energy to the left
     * @param b energy straight ahead
     * @param c energy to the right
     * @return -1 if a is minimum, 0 if b is minimum, or +1 if c is minimum
     */
    private static int min3(int a, int b, int c) {
        int move;
        if (a <= b) {
            if (a <= c) {
                move = -1;
            } else {
                move = +1;
            }
        } else {
            if (b <= c) {
                move = 0;
            } else {
                move = +1;
            }
        }
        return move;
    }

}
