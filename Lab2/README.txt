Exchange Rates Database App

Aplikacja konsolowa napisana w języku C# 
(.NET 8.0) umożliwiająca pobieranie kursów walut z zewnętrznego API oraz zapisywanie ich w lokalnej bazie danych SQLite z wykorzystaniem Entity Framework Core.

Funkcjonalności
Pobieranie aktualnych kursów walut z API Open Exchange Rates.
Deserializacja danych JSON do obiektów C#.
Zapisywanie kursów walut w bazie danych SQLite.
Wykorzystanie relacji pomiędzy encjami (Currency – ExchangeRate).
Pobieranie danych z API tylko w przypadku ich braku w bazie.
Wyświetlanie wszystkich walut zapisanych w bazie.
Filtrowanie kursów (np. kursy mniejsze niż 5.0).
Sortowanie kursów według daty pobrania.
Struktura projektu
Program.cs – główna logika aplikacji, obsługa API, operacje na bazie danych.
ExchangeRates.cs – klasa do deserializacji odpowiedzi JSON z API.
CurrencyContext – kontekst bazy danych (Entity Framework Core).
Currency – encja reprezentująca walutę.
ExchangeRate – encja reprezentująca kurs waluty (relacja wiele-do-jednego z Currency).

Technologie
C#
.NET 8.0
Entity Framework Core 8
SQLite
LINQ
System.Text.Json
Open Exchange Rates API
Baza danych

Aplikacja wykorzystuje lokalną bazę danych SQLite:

ExchangeRates.db

Baza tworzona jest automatycznie przy pierwszym uruchomieniu programu.

Sposób działania
Użytkownik podaje kwotę oraz kod waluty (np. 10 PLN).
Program sprawdza, czy dana waluta istnieje w bazie danych:
jeśli tak – używa najnowszego zapisanego kursu,
jeśli nie – pobiera dane z API i zapisuje je w bazie.
Następnie przelicza podaną kwotę na walutę bazową (USD).
Na końcu wyświetla:
wszystkie zapisane waluty,
przefiltrowane kursy spełniające zadany warunek.
