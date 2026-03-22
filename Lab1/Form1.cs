using System;
using System.Drawing;
using System.Windows.Forms;
using KnapsackApp; 

namespace KnapsackApp.GUI
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnSolve_Click(object sender, EventArgs e)
        {
            // 1. Resetowanie kolorów t³a
            txtNumberOfItems.BackColor = Color.White;
            txtSeed.BackColor = Color.White;
            txtCapacity.BackColor = Color.White;

            bool isValid = true;
            int n = 0, seed = 0, capacity = 0;

            // 2. Walidacja danych wejœciowych
            if (!int.TryParse(txtNumberOfItems.Text, out n) || n <= 0)
            {
                txtNumberOfItems.BackColor = Color.LightPink; // Podœwietlenie
                isValid = false;
            }

            if (!int.TryParse(txtSeed.Text, out seed))
            {
                txtSeed.BackColor = Color.LightPink;
                isValid = false;
            }

            if (!int.TryParse(txtCapacity.Text, out capacity) || capacity <= 0)
            {
                txtCapacity.BackColor = Color.LightPink;
                isValid = false;
            }

            if (!isValid)
            {
                MessageBox.Show("Wprowadzone dane s¹ niepoprawne.",
                                "B³¹d wprowadzania danych", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            try
            {
                Problem problem = new Problem(n, seed);
                Result result = problem.Solve(capacity);

                // Wyœwietlanie wyniku w multiline textbox
                txtResult.Text = $"=== WYGENEROWANY PROBLEM ===\r\n{problem}\r\n" +
                                 $"=== ROZWI¥ZANIE ===\r\n{result}";
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Wyst¹pi³ nieoczekiwany b³¹d podczas obliczeñ:\n{ex.Message}", "B³¹d", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }
}