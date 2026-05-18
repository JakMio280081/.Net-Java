package knapsack;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<Integer> itemIndices = new ArrayList<>();
    public int totalValue = 0;
    public int totalWeight = 0;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Rozwiązanie ---\n");
        for (int index : itemIndices) {
            sb.append("Przedmiot nr: ").append(index).append("\n");
        }
        sb.append("Suma wag: ").append(totalWeight).append("\n");
        sb.append("Suma wartości: ").append(totalValue);
        return sb.toString();
    }
}

Main.java

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
