#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <unordered_map>
#include <mutex>
#include <thread>
#include <sstream>
#include <algorithm>

using namespace std;

struct Informacion
{
    int lineasNoVacias = 0;
    int palabras = 0;
    int caracteres = 0;
    int espacios = 0;
    unordered_map<string, int> frecuencias;
    string palabraMasFrecuente;
    int cantidadMasFrecuente = 0;
};

mutex mtx;
Informacion info;

int ContarEspacios(const string& linea)
{
    return count(linea.begin(), linea.end(), ' ');
}

void ProcesarLinea(const string& linea)
{
    int palabras = 0;
    int caracteres = 0;
    int espacios = 0;
    unordered_map<string, int> frecuenciaPalabras;

    string palabra;
    istringstream iss(linea);

    while (iss >> palabra)
    {
        frecuenciaPalabras[palabra]++;
        palabras++;
        caracteres += palabra.length();
    }

    espacios = ContarEspacios(linea);

    lock_guard<mutex> guard(mtx);
    info.lineasNoVacias++;
    info.palabras += palabras;
    info.caracteres += caracteres;
    info.espacios += espacios;
    for (const auto& palabra : frecuenciaPalabras)
    {
        info.frecuencias[palabra.first] += palabra.second;
    }
}

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        cerr << "Se requiere el argumento del archivo" << endl;
        return 1;
    }

    ifstream archivo(argv[1]);
    if (!archivo)
    {
        cerr << "El archivo " << argv[1] << " no existe" << endl;
        return 1;
    }

    vector<thread> hilos;

    string linea;
    while (getline(archivo, linea))
    {
        if (!linea.empty())
        {
            hilos.emplace_back(ProcesarLinea, linea);
        }
    }

    for (auto& hilo : hilos)
    {
        hilo.join();
    }

    for (const auto& palabra : info.frecuencias)
    {
        if (palabra.second > info.cantidadMasFrecuente)
        {
            info.cantidadMasFrecuente = palabra.second;
            info.palabraMasFrecuente = palabra.first;
        }
    }

    cout << "Cantidad de líneas no vacías: " << info.lineasNoVacias << endl;
    cout << "Cantidad de palabras: " << info.palabras << endl;
    cout << "Cantidad de caracteres: " << info.caracteres << endl;
    cout << "Cantidad de espacios: " << info.espacios << endl;
    cout << "Palabra más frecuente: \"" << info.palabraMasFrecuente << "\" con cantidad = " << info.cantidadMasFrecuente << endl;

    return 0;
}

