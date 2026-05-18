package knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

public class Problem {
    public int n;
    public long seed;
    public int lowerBound = 1;
    public int upperBound = 10;

    public List<Integer> weights = new ArrayList<>();
    public List<Integer> values = new ArrayList<>();

    public Problem(int n, long seed) {
        this.n = n;
        this.seed = seed;
        Random rand = new Random(seed);

        for (int i = 0; i < n; i++) {
            int weight = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            int value = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            weights.add(weight);
            values.add(value);
        }
    }

    public Result Solve(int capacity) {
        Result result = new Result();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);

        indices.sort((a, b) -> {
            double ratioA = (double) values.get(a) / weights.get(a);
            double ratioB = (double) values.get(b) / weights.get(b);
            return Double.compare(ratioB, ratioA);
        });

        int currentCapacity = capacity;
        for (int i : indices) {
            while (currentCapacity >= weights.get(i)) {
                result.itemIndices.add(i);
                result.totalValue += values.get(i);
                result.totalWeight += weights.get(i);
                currentCapacity -= weights.get(i);
            }
        }
        return result;
    }

    @Override
    public String toString() { // Przeciążenie
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("No: ").append(i)
                    .append(" v: ").append(values.get(i))
                    .append(" w: ").append(weights.get(i)).append("\n");
        }
        return sb.toString();
    }
}
