#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h>

void ManejarOpcion(char);
void GenerarHijos(char *);

void ManejarOpcion(char opcion)
{
    switch (opcion) {
        case 'B':
            GenerarHijos("EF");
            break;
        case 'D':
            GenerarHijos("G");
            break;
        case 'F':
            GenerarHijos("HI");
            break;
        case 'I':
            GenerarHijos("J");
            break;
        default:
            break;
    }
}

void GenerarHijos(char *hijos)
{
    int cantidad_hijos = strlen(hijos);
    while(*hijos!='\0')
    {
        char caracter = *hijos;
        pid_t pid = fork();
        if(pid==0)
        {
                pid_t pidMio = getpid();
                pid_t pidPadre = getppid();
                printf("Soy el proceso %c mi pid es: %d y el de mi padre es: %d\n",caracter,pidMio, pidPadre);
                ManejarOpcion(caracter);
                sleep(50);
                exit(0);
        }
        hijos++;
    }

    for(int i=0;i<cantidad_hijos;i++){
        wait(NULL);
    }
}

int main(int argc, char *argv[])
{
    printf("Soy el proceso A mi pid es: %d\n", getpid());
    GenerarHijos("BCD");

    return EXIT_SUCCESS;
}
