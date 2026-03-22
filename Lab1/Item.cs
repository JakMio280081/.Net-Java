namespace KnapsackApp
{
    internal class Item
    {
        public int Id { get; set; }
        public int Value { get; set; }
        public int Weight { get; set; }

        public double Ratio => (double)Value / Weight;

        public Item(int id, int value, int weight)
        {
            Id = id;
            Value = value;
            Weight = weight;
        }

        public override string ToString()
        {
            return $"Item {Id}: value={Value}, weight={Weight}, ratio={Ratio:F2}";
        }
    }
}