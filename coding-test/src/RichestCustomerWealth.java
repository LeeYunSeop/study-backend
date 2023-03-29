import java.util.Arrays;

public class RichestCustomerWealth {

    public static void main(String[] args) {
        int[][] testCase = new int[][]{{1,2,3},{3,2,1},{7,1,10}};
        System.out.println("testCase = " + maximumWealth(testCase));
    }

    public static int maximumWealth(int[][] accounts) {
        int[] result = new int[accounts.length];
        for(int i = 0; i < accounts.length; i++) {
            for(int j = 0; j < accounts[i].length; j++) {
                result[i] += accounts[i][j];
            }
        }
        Arrays.sort(result);

        System.out.println("result = " + Arrays.toString(result));

        return result[result.length-1];
    }

    public static int maximumWealthMath(int[][] accounts) {

        int max = 0;

        for (int[] i : accounts) {
            int sum = 0;
            for (int j : i) {
                sum += j;
            }
            max = Math.max(max, sum);
        }

        return max;
    }

    public static int maximumWealthStream(int[][] accounts) {

        return Arrays.stream(accounts)
                .mapToInt(m -> Arrays.stream(m).sum())
                .max()
                .getAsInt();
    }
}