using Microsoft.VisualStudio.TestTools.UnitTesting;
using KnapsackApp;
using System.Collections.Generic;
using System.Linq;

namespace KnapsackApp.Tests
{
    [TestClass]
    public class ProblemTests
    {
        // 1. Sprawdzenie, czy jeśli co najmniej jeden przedmiot spełnia ograniczenia, to zwrócono co najmniej jeden element.
        [TestMethod]
        public void Solve_AtLeastOneItemFits_ReturnsAtLeastOneItem()
        {
            Problem problem = new Problem(0, 1); 
            problem.Items = new List<Item>
            {
                new Item(1, 10, 5), // Waga 5, zmieści się
                new Item(2, 5, 15)  // Waga 15, nie zmieści się
            };
            int capacity = 10;

            Result result = problem.Solve(capacity);

            Assert.IsTrue(result.Items.Count > 0, "Rozwiązanie powinno zawierać co najmniej jeden przedmiot.");
            Assert.IsTrue(result.Items.Contains(1), "W plecaku powinien znaleźć się przedmiot o ID 1.");
        }

        // 2. Sprawdzenie, czy jeśli żaden przedmiot nie spełnia ograniczeń, to zwrócono puste rozwiązanie.
        [TestMethod]
        public void Solve_NoItemsFit_ReturnsEmptySolution()
        {
            Problem problem = new Problem(0, 1);
            problem.Items = new List<Item>
            {
                new Item(1, 10, 20),
                new Item(2, 5, 15)
            };
            int capacity = 10; // Żaden przedmiot się nie zmieści

            Result result = problem.Solve(capacity);

            Assert.AreEqual(0, result.Items.Count, "Rozwiązanie powinno być puste.");
            Assert.AreEqual(0, result.TotalValue, "Całkowita wartość powinna wynosić 0.");
            Assert.AreEqual(0, result.TotalWeight, "Całkowita waga powinna wynosić 0.");
        }

        // 3. Sprawdzenie poprawności wyniku dla konkretnej instancji.
        [TestMethod]
        public void Solve_SpecificInstance_ReturnsCorrectResult()
        {
            Problem problem = new Problem(0, 1);
            problem.Items = new List<Item>
            {
                // Przedmiot 1: Ratio = 2.0
                new Item(1, 10, 5),
                // Przedmiot 2: Ratio = 3.0 
                new Item(2, 6, 2),  
                // Przedmiot 3: Ratio = 1.0
                new Item(3, 4, 4)
            };
            int capacity = 8;

            // Weźmie przedmiot 2 (waga 2, zostaje 6)
            // Weźmie przedmiot 1 (waga 5, zostaje 1)
            // Przedmiot 3 się nie zmieści.
            // Oczekiwana wartość: 6 + 10 = 16. Oczekiwana waga: 2 + 5 = 7.
            Result result = problem.Solve(capacity);

            Assert.AreEqual(2, result.Items.Count);
            Assert.IsTrue(result.Items.Contains(1));
            Assert.IsTrue(result.Items.Contains(2));
            Assert.AreEqual(16, result.TotalValue);
            Assert.AreEqual(7, result.TotalWeight);
        }

        // 4. Sprawdzenie, czy konstruktor generuje dokładnie zadaną liczbę przedmiotów.
        [TestMethod]
        public void Problem_Constructor_GeneratesCorrectNumberOfItems()
        {
            int expectedCount = 15;
            int seed = 123;

            Problem problem = new Problem(expectedCount, seed);

            Assert.AreEqual(expectedCount, problem.Items.Count, "Konstruktor powinien wygenerować dokładnie N przedmiotów.");
            Assert.AreEqual(expectedCount, problem.N, "Właściwość N powinna odpowiadać liczbie zadeklarowanej.");
        }

        // 5. Sprawdzenie, czy suma wag zwróconych przedmiotów nigdy nie przekracza pojemności plecaka.
        [TestMethod]
        public void Solve_ReturnedTotalWeight_DoesNotExceedCapacity()
        {
            Problem problem = new Problem(100, 42); // 100 losowych przedmiotów
            int capacity = 50;

            Result result = problem.Solve(capacity);

            Assert.IsTrue(result.TotalWeight <= capacity, "Całkowita waga przedmiotów w plecaku nie może przekraczać jego pojemności.");

            // Sprawdzenie czy TotalWeight pokrywa się z faktyczną wagą przedmiotów
            int actualWeightSum = problem.Items.Where(i => result.Items.Contains(i.Id)).Sum(i => i.Weight);
            Assert.AreEqual(actualWeightSum, result.TotalWeight, "Właściwość TotalWeight musi zgadzać się z sumą wag wyciągniętych obiektów.");
        }
    }
}