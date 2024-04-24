#include <iostream>
#include <vector>
#include <thread>
#include <random>
#include <chrono>
#include <cstdlib>

using namespace std;

int N_COLUMNAS_Y_FILAS = 0;
int N_HILOS = 0;
int ESCALAR = 2;

vector<vector<int>> GenerarMatriz()
{
    vector<vector<int>> matriz(N_COLUMNAS_Y_FILAS, vector<int>(N_COLUMNAS_Y_FILAS));

    for (int i = 0; i < N_COLUMNAS_Y_FILAS; ++i)
    {
        for (int j = 0; j < N_COLUMNAS_Y_FILAS; ++j)
        {
            matriz[i][j] = rand() % 10;
        }
    }

    return matriz;
}

vector<vector<int>> MultiplicarMatrizRS(vector<vector<int>> matriz)
{

    vector<vector<int>> matrizRS(N_COLUMNAS_Y_FILAS, vector<int>(N_COLUMNAS_Y_FILAS));

    for (int i = 0; i < N_COLUMNAS_Y_FILAS; ++i)
    {
        for (int j = 0; j < N_COLUMNAS_Y_FILAS; ++j)
        {
                matrizRS[i][j] += matriz[i][j] * ESCALAR;
        }
    }

    return matrizRS;
}

void MultiplicarFila(vector<vector<int>> &matriz, vector<vector<int>> &matrizRC, int inicio, int final)
{
    for (int i = inicio; i < final; i++)
        for (int j = 0; j < N_COLUMNAS_Y_FILAS; j++)
        {
            matrizRC[i][j] += matriz[i][j] * ESCALAR;
        }
}

vector<vector<int>> GenerarMatrizRC(vector<vector<int>> matriz)
{
    vector<thread> threads;

    div_t resultado = div(N_COLUMNAS_Y_FILAS, N_HILOS);

    vector<vector<int>> matrizRC(N_COLUMNAS_Y_FILAS, vector<int>(N_COLUMNAS_Y_FILAS));

    int cociente = resultado.quot;
    int resto = resultado.rem;

    int inicio = 0;
    int final = 0;

    for (int i = 0; i < N_HILOS; ++i)
    {
        if (i == N_HILOS - 1)
        {
            final += cociente + resto;
        }
        else
        {
            final += cociente;
        }

        threads.emplace_back(MultiplicarFila, ref(matriz),ref(matrizRC), inicio, final);

        inicio = final;


    }

    for (auto &thread : threads)
    {
        thread.join();
    }

    return matrizRC;
}

bool CompararMatrices(vector<vector<int>> matrizRS, vector<vector<int>> matrizRC){

    for (int i = 0; i < N_COLUMNAS_Y_FILAS; ++i)
    {
        for (int j = 0; j < N_COLUMNAS_Y_FILAS; ++j)
        {
            if(matrizRS[i][j] != matrizRC[i][j])
            {
                cout<< matrizRC[i][j] << " != " << matrizRS[i][j] << endl;
                return false;
            }
        }
    }
    return true;
}

int main(int argc, char *argv[])
{
    if (argc != 3)
    {
        std::cerr << "Uso: " << argv[0] << " <cantidad_filas> <cantidad_hilos>" << std::endl;
        return 0;
    }

    N_COLUMNAS_Y_FILAS = atoi(argv[1]);
    N_HILOS = atoi(argv[2]);

    vector<vector<int>> matriz = GenerarMatriz();

    auto start_sequential = chrono::steady_clock::now();
    vector<vector<int>> matrizRS = MultiplicarMatrizRS(matriz);
    auto end_sequential = chrono::steady_clock::now();
    chrono::duration<double> sequential_time = end_sequential - start_sequential;
    auto start_concurrent = chrono::steady_clock::now();
    vector<vector<int>> matrizRC = GenerarMatrizRC(matriz);
    auto end_concurrent = chrono::steady_clock::now();
    chrono::duration<double> concurrent_time = end_concurrent - start_concurrent;

    if(CompararMatrices(matrizRS, matrizRC))
    {
        cout << "Tiempo secuencial: " << sequential_time.count() << " segundos" << endl;
        cout << "Tiempo concurrente: " << concurrent_time.count() << " segundos" << endl;
    }
    else
    {
        cout << "Las matrices son diferentes" << endl;
    }

    return 0;
}
