package Utils;

import java.util.ArrayList;

public class SparseTable {
    // Fast log base 2 logarithm lookup table for i, 1 <= i <= n
    private int[] log2;

    // The sparse table values.
    private long[][] dp;

    // Index Table (IT) associated with the values in the sparse table.
    private int[][] it;
    private final ArrayList<Integer> list;
    public SparseTable(ArrayList<Integer> list){
        this.list = list;

        init();
    }

    public int getMaxIndexFromSequence(int left, int right){
        return maxQueryIndex(left, right);
    }

    private void init() {
        // The number of elements in the original input array.
        int n = list.size();

        // Tip: to get the floor of the logarithm base 2 in Java you can also do:
        // Integer.numberOfTrailingZeros(Integer.highestOneBit(n)).

        // The maximum power of 2 needed. This value is floor(log2(n))
        int p = (int) (Math.log(n) / Math.log(2));
        dp = new long[p + 1][n];
        it = new int[p + 1][n];

        for (int i = 0; i < n; i++) {
            dp[0][i] = list.get(i);
            it[0][i] = i;
        }

        log2 = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            log2[i] = log2[i / 2] + 1;
        }

        // Build sparse table combining the values of the previous intervals.
        for (int i = 1; i <= p; i++) {
            for (int j = 0; j + (1 << i) <= n; j++) {
                long leftInterval = dp[i - 1][j];
                long rightInterval = dp[i - 1][j + (1 << (i - 1))];

                dp[i][j] = Math.max(leftInterval, rightInterval);
                // Propagate the index of the best value
                if (leftInterval >= rightInterval) {
                    it[i][j] = it[i - 1][j];
                } else {
                    it[i][j] = it[i - 1][j + (1 << (i - 1))];
                }
            }
        }
    }

    private int maxQueryIndex(int l, int r) {
        int len = r - l + 1;
        int p = log2[len];
        long leftInterval = dp[p][l];
        long rightInterval = dp[p][r - (1 << p) + 1];
        if (leftInterval >= rightInterval) {
            return it[p][l];
        } else {
            return it[p][r - (1 << p) + 1];
        }
    }
}
