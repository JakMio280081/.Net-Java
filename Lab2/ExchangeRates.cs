using System.Text.Json.Serialization;
using System.Collections.Generic;

public class ExchangeRatesResponse
{
    public string disclaimer { get; set; }
    public string license { get; set; }
    public long timestamp { get; set; }
    public string @base { get; set; }
    public Dictionary<string, double> rates { get; set; }
}
