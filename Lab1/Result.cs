using System.Text;

namespace KnapsackApp
{
    internal class Result
    {
        public List<int> Items { get; set; } = new List<int>();
        public int TotalValue { get; set; }
        public int TotalWeight { get; set; }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine("Items in knapsack:");
            foreach (var item in Items)
                sb.AppendLine($"Item {item}");

            sb.AppendLine($"Total value: {TotalValue}");
            sb.AppendLine($"Total weight: {TotalWeight}");

            return sb.ToString();
        }
    }
}