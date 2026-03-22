# Knapsack Problem Solver

Aplikacja desktopowa napisana w języku C# (WinForms) służąca do rozwiązywania problemu plecakowego. 
Projekt prezentuje separację logiki od interfejsu graficznego oraz wykorzystanie testów jednostkowych.

## Funkcjonalności

- Generowanie zestawu przedmiotów o losowej wadze i wartości na podstawie podanego ziarna (seed).
- Wyznaczanie rozwiązania problemu plecakowego dla określonej pojemności.
- Interfejs graficzny z walidacją danych wejściowych.

## Struktura projektu

- KnapsackApp - rdzeń aplikacji i logika (klasy Item, Problem, Result).
- KnapsackApp.GUI - interfejs użytkownika (Windows Forms).
- KnapsackApp.Tests - projekt testowy weryfikujący poprawność działania algorytmu (MSTest).

## Technologie

- C#
- .NET (Windows Forms)
- MSTest
- Visual Studio
