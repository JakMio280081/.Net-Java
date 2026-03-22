using System.Text;

namespace KnapsackApp
{
    internal class Problem
    {
        public int N { get; set; }
        public List<Item> Items { get; set; }

        public Problem(int n, int seed)
        {
            N = n;
            Items = new List<Item>();

            Random random = new Random(seed);

            for (int i = 0; i < n; i++)
            {
                int value = random.Next(1, 11);
                int weight = random.Next(1, 11);

                Items.Add(new Item(i + 1, value, weight));
            }
        }

        public Result Solve(int capacity)
        {
            Result result = new Result();

            var sortedItems = Items
                .OrderByDescending(i => i.Ratio)
                .ToList();

            int currentWeight = 0;

            foreach (var item in sortedItems)
            {
                if (currentWeight + item.Weight <= capacity)
                {
                    result.Items.Add(item.Id);
                    result.TotalWeight += item.Weight;
                    result.TotalValue += item.Value;

                    currentWeight += item.Weight;
                }
            }

            return result;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine($"Number of items: {N}");
            sb.AppendLine("Items:");

            foreach (var item in Items)
                sb.AppendLine(item.ToString());

            return sb.ToString();
        }
    }
}