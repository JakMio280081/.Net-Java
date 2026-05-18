package knapsack;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Give number of items:");
        int n = scanner.nextInt();

        System.out.println("Give seed:");
        long seed = scanner.nextLong();

        System.out.println("Give knapsack capacity:");
        int capacity = scanner.nextInt();

        Problem problem = new Problem(n, seed);
        System.out.println("\nInstancja problemu:");
        System.out.println(problem.toString());

        Result result = problem.Solve(capacity);
        System.out.println("\nWynik:");
        System.out.println(result.toString());
    }
}
