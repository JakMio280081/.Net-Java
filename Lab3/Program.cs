using System;
using System.Diagnostics;
using System.Threading.Tasks;

namespace ParallelMatrixMultiplication
{
    class Matrix
    {
        public int Rows { get; }
        public int Cols { get; }
        public double[,] Data { get; }

        private static Random rand = new Random();

        public Matrix(int rows, int cols)
        {
            Rows = rows;
            Cols = cols;
            Data = new double[rows, cols];
        }

        public void FillRandom()
        {
            for (int i = 0; i < Rows; i++)
                for (int j = 0; j < Cols; j++)
                    Data[i, j] = rand.Next(1, 10);
        }

        public static Matrix MultiplyParallel(Matrix A, Matrix B, int threadCount)
        {
            Matrix result = new Matrix(A.Rows, B.Cols);

            ParallelOptions options = new ParallelOptions
            {
                MaxDegreeOfParallelism = threadCount
            };

            Parallel.For(0, A.Rows, options, i =>
            {
                for (int j = 0; j < B.Cols; j++)
                {
                    double sum = 0;

                    for (int k = 0; k < A.Cols; k++)
                        sum += A.Data[i, k] * B.Data[k, j];

                    result.Data[i, j] = sum;
                }
            });

            return result;
        }
    }

    class Program
    {
        static void TestParallel(int size, int threads)
        {
            int repetitions = 5;
            long total = 0;

            for (int r = 0; r < repetitions; r++)
            {
                Matrix A = new Matrix(size, size);
                Matrix B = new Matrix(size, size);

                A.FillRandom();
                B.FillRandom();

                Stopwatch sw = Stopwatch.StartNew();

                var result = Matrix.MultiplyParallel(A, B, threads);

                sw.Stop();
                total += sw.ElapsedMilliseconds;
            }

            Console.WriteLine($"PARALLEL | Size {size} | Threads {threads} | {total / repetitions} ms");
        }

        static void TestThread(int size, int threads)
        {
            int repetitions = 5;
            long total = 0;

            for (int r = 0; r < repetitions; r++)
            {
                Matrix A = new Matrix(size, size);
                Matrix B = new Matrix(size, size);

                A.FillRandom();
                B.FillRandom();

                Stopwatch sw = Stopwatch.StartNew();

                var result = ThreadMatrixMultiplier.Multiply(A, B, threads);

                sw.Stop();
                total += sw.ElapsedMilliseconds;
            }

            Console.WriteLine($"THREAD   | Size {size} | Threads {threads} | {total / repetitions} ms");
        }

        static void Main(string[] args)
        {
            int size = 500;

            Console.WriteLine("Matrix multiplication comparison\n");

            for (int threads = 1; threads <= Environment.ProcessorCount * 2; threads++)
            {
                TestParallel(size, threads);
                TestThread(size, threads);
                Console.WriteLine();
            }

            Console.ReadKey();
        }
    }
}
