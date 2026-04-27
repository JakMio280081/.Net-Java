Celem programu jest porównanie wydajności mnożenia macierzy z użyciem wysokopoziomowej biblioteki Parallel oraz niskopoziomowych wątków Thread.

Funkcjonalność
generowanie losowych macierzy kwadratowych
mnożenie macierzy równolegle (Parallel.For)
mnożenie macierzy z użyciem klasy Thread
testy dla różnych liczb wątków
uśrednianie czasu z kilku powtórzeń
porównanie wydajności obu podejść

Parametry
rozmiar macierzy: size (domyślnie 500)
liczba wątków: od 1 do 2 × liczba rdzeni CPU
liczba powtórzeń pomiaru: 5
Przykładowy wynik
PARALLEL | Size 500 | Threads 4 | 120 ms
THREAD   | Size 500 | Threads 4 | 135 ms

Biblioteka Parallel zwykle osiąga lepszą wydajność dzięki zarządzaniu pulą wątków i optymalizacji podziału pracy, natomiast Thread daje większą kontrolę kosztem większego narzutu.
