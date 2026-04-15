using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

public class Currency
{
    public int Id { get; set; }
    public string Code { get; set; }

    public List<ExchangeRate> Rates { get; set; } = new List<ExchangeRate>();
}

public class ExchangeRate
{
    public int Id { get; set; }
    public double Rate { get; set; }
    public DateTime FetchDate { get; set; }

    public int CurrencyId { get; set; }
    public Currency Currency { get; set; }
}
public class CurrencyContext : DbContext
{
    public DbSet<Currency> Currencies { get; set; }
    public DbSet<ExchangeRate> ExchangeRates { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseSqlite("Data Source=ExchangeRates.db");
    }
}
class Program
{
    static async Task Main(string[] args)
    {
        //Tworzenie bazy
        using var db = new CurrencyContext();
        db.Database.EnsureCreated();

        Console.WriteLine("Podaj kwotę i kod waluty (np. 10 PLN): ");
        string input = Console.ReadLine()?.ToUpper();

        if (string.IsNullOrWhiteSpace(input)) return;

        string[] parts = input.Split(' ', StringSplitOptions.RemoveEmptyEntries);

        if (parts.Length != 2 || !double.TryParse(parts[0], out double amount))
        {
            Console.WriteLine("Błędny format! Podaj np. 10 PLN");
            return;
        }

        string currencyCode = parts[1];
        double rate = 0;
        string baseCurrency = "USD";

        var existingCurrency = db.Currencies
            .Include(c => c.Rates)
            .FirstOrDefault(c => c.Code == currencyCode);

        if (existingCurrency != null && existingCurrency.Rates.Any())
        {
            Console.WriteLine("Znaleziono walutę w bazie.");

            //Sortowanie
            var latestRate = existingCurrency.Rates.OrderByDescending(r => r.FetchDate).First();
            rate = latestRate.Rate;
        }
        else
        {
            Console.WriteLine("Brak waluty w bazie. Pobieram nowe dane...");

            string appId = "f33dab40bf9146ebba331301e7e5b89b";
            string url = $"https://openexchangerates.org/api/latest.json?app_id={appId}";

            using HttpClient client = new HttpClient();
            try
            {
                string response = await client.GetStringAsync(url);
                ExchangeRatesResponse data = JsonSerializer.Deserialize<ExchangeRatesResponse>(response);
                baseCurrency = data.@base;

                if (data.rates.ContainsKey(currencyCode))
                {
                    rate = data.rates[currencyCode];

                    //Zapis do bazy

                    if (existingCurrency == null)
                    {
                        existingCurrency = new Currency { Code = currencyCode };
                        db.Currencies.Add(existingCurrency);
                    }

                    var newExchangeRate = new ExchangeRate
                    {
                        Rate = rate,
                        FetchDate = DateTime.Now,
                        Currency = existingCurrency
                    };

                    db.ExchangeRates.Add(newExchangeRate);
                    db.SaveChanges();

                    Console.WriteLine("Zapisano nowy kurs w bazie!");
                }
                else
                {
                    Console.WriteLine("Nie znaleziono podanej waluty w API.");
                    return;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Wystąpił błąd podczas połączenia z API:\n" + ex.Message);
                return;
            }
        }

        //Wynik
        double result = amount / rate;
        Console.WriteLine($"\nWynik: {amount} {currencyCode} = {result:F2} {baseCurrency}\n");


        //Zapytania
        Console.WriteLine("WSZYSTKIE WALUTY W BAZIE");
        var allCurrencies = db.Currencies
            .Include(c => c.Rates)
            .OrderBy(c => c.Code)
            .ToList();

        foreach (var c in allCurrencies)
        {
            var bestRate = c.Rates.OrderByDescending(r => r.FetchDate).FirstOrDefault();
            Console.WriteLine($"- {c.Code}: {bestRate?.Rate} (zaktualizowano: {bestRate?.FetchDate})");
        }

        Console.WriteLine("\nFILTROWANIE: Znalezione kursy mniejsze niż 5.0");
        var filteredRates = db.ExchangeRates
            .Include(e => e.Currency)
            .Where(e => e.Rate < 5.0)
            .OrderBy(e => e.Rate)
            .ToList();

        foreach (var r in filteredRates)
        {
            Console.WriteLine($"- {r.Currency.Code}: {r.Rate}");
        }
    }
}
