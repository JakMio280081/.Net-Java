using System.Runtime.CompilerServices;

[assembly: InternalsVisibleTo("KnapsackApp.Tests"), InternalsVisibleTo("KnapsackApp.GUI")]

namespace KnapsackApp
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Enter number of items:");
            int n = int.Parse(Console.ReadLine());

            Console.WriteLine("Enter seed:");
            int seed = int.Parse(Console.ReadLine());

            Console.WriteLine("Enter knapsack capacity:");
            int capacity = int.Parse(Console.ReadLine());

            Problem problem = new Problem(n, seed);

            Console.WriteLine("\nGenerated problem:");
            Console.WriteLine(problem);

            Result result = problem.Solve(capacity);

            Console.WriteLine("Solution:");
            Console.WriteLine(result);
        }
    }
}