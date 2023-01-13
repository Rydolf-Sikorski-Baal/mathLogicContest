package Utils;

import java.util.ArrayList;

public class SparseTable {
    // Fast log base 2 logarithm lookup table for i, 1 <= i <= n
    private int[] log2;

    // The sparse table values.
    private long[][] dpLeft;
    private long[][] dpRight;

    // Index Table (IT) associated with the values in the sparse table.
    private int[][] itLeft;
    private int[][] itRight;
    private final ArrayList<Integer> list;
    public SparseTable(ArrayList<Integer> list){
        this.list = list;

        init();
    }

    public int getLeftMaxIndexFromSequence(int left, int right){
        return maxQueryLeftIndex(left, right);
    }

    public int getRightMaxIndexFromSequence(int left, int right){
        return maxQueryRightIndex(left, right);
    }

    private void init() {
        buildLeftDp();
        buildRightDp();
    }

    private void buildLeftDp(){
        // The number of elements in the original input array.
        int n = list.size();

        // The maximum power of 2 needed. This value is floor(log2(n))
        int p = (int) (Math.log(n) / Math.log(2));
        dpLeft = new long[p + 1][n];
        itLeft = new int[p + 1][n];

        for (int i = 0; i < n; i++) {
            dpLeft[0][i] = list.get(i);
            itLeft[0][i] = i;
        }

        log2 = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            log2[i] = log2[i / 2] + 1;
        }

        // Build sparse table combining the values of the previous intervals.
        for (int i = 1; i <= p; i++) {
            for (int j = 0; j + (1 << i) <= n; j++) {
                long leftInterval = dpLeft[i - 1][j];
                long rightInterval = dpLeft[i - 1][j + (1 << (i - 1))];

                dpLeft[i][j] = Math.max(leftInterval, rightInterval);
                // Propagate the index of the best value
                if (leftInterval >= rightInterval) {
                    itLeft[i][j] = itLeft[i - 1][j];
                } else {
                    itLeft[i][j] = itLeft[i - 1][j + (1 << (i - 1))];
                }
            }
        }
    }

    private void buildRightDp(){
        // The number of elements in the original input array.
        int n = list.size();

        // The maximum power of 2 needed. This value is floor(log2(n))
        int p = (int) (Math.log(n) / Math.log(2));
        dpRight = new long[p + 1][n];
        itRight = new int[p + 1][n];

        for (int i = 0; i < n; i++) {
            dpRight[0][i] = list.get(i);
            itRight[0][i] = i;
        }

        log2 = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            log2[i] = log2[i / 2] + 1;
        }

        // Build sparse table combining the values of the previous intervals.
        for (int i = 1; i <= p; i++) {
            for (int j = 0; j + (1 << i) <= n; j++) {
                long leftInterval = dpRight[i - 1][j];
                long rightInterval = dpRight[i - 1][j + (1 << (i - 1))];

                dpRight[i][j] = Math.max(leftInterval, rightInterval);
                // Propagate the index of the best value
                if (leftInterval > rightInterval) {
                    itRight[i][j] = itRight[i - 1][j];
                } else {
                    itRight[i][j] = itRight[i - 1][j + (1 << (i - 1))];
                }
            }
        }
    }

    private int maxQueryLeftIndex(int l, int r) {
        int len = r - l + 1;
        int p = log2[len];
        long leftInterval = dpLeft[p][l];
        long rightInterval = dpLeft[p][r - (1 << p) + 1];

        if (leftInterval >= rightInterval) {
            return itLeft[p][l];
        } else {
            return itLeft[p][r - (1 << p) + 1];
        }
    }

    private int maxQueryRightIndex(int l, int r) {
        int len = r - l + 1;
        int p = log2[len];
        long leftInterval = dpRight[p][l];
        long rightInterval = dpRight[p][r - (1 << p) + 1];

        if (leftInterval > rightInterval) {
            return itRight[p][l];
        } else {
            return itRight[p][r - (1 << p) + 1];
        }
    }
}
