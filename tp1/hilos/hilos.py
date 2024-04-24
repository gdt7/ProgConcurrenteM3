import threading
from random import randrange
import numpy
import sys
import argparse
import math
import time


n = 0
inicial = [[]]
concurrente = [[]]
secuencial = [[]]
ESCALAR = 5

def multiplyRows(id,filaDesde,filaHasta):
    global concurrente,inicial,ESCALAR,n
    for i in range(filaDesde,filaHasta + 1):
          for j in range(n):
            concurrente[i][j] = (inicial[i][j] * ESCALAR);


def concurrentExecute(threads) :
    global inicial,concurrente,secuencial,ESCALAR,n
    my_threads = []
    filasPorHilos = n / threads
    for i in range(1,threads + 1):
      filaDesde = math.ceil((i - 1) * filasPorHilos)
      filaHasta = math.ceil((i * filasPorHilos) -1)
      my_threads.append(threading.Thread(target=multiplyRows,args=(i,filaDesde,filaHasta)));

    for i in range(threads):
      my_threads[i].start()

    for i in range(threads):
      my_threads[i].join()

def checkValidityOfMatrix():
   global n
   for i in range(n):
      for j in range(n):
         if secuencial[i][j] != concurrente[i][j] :
           print("ERROR",i,j)
           break

def main():
    global inicial,concurrente,secuencial,ESCALAR,n
    parser = argparse.ArgumentParser("argumentos_matriz")
    parser.add_argument("n", help="", type=int)
    args = parser.parse_args()
    n = args.n
    inicial = numpy.zeros((args.n, args.n))
    concurrente = numpy.zeros((args.n, args.n))
    secuencial = numpy.zeros((args.n, args.n))

    for i in range(args.n):
      for j in range(args.n):
        inicial[i][j] = randrange(10)

    startSecuencial = time.time()
    for i in range(args.n):
      for j in range(args.n):
         secuencial[i][j] = (inicial[i][j] * ESCALAR);
    endSecuencial = time.time()
    print("Tiempo secuencial : ", (endSecuencial - startSecuencial) * 1000 , "    milisegundos")


    start1Thread = time.time()
    concurrentExecute(1)
    end1Thread = time.time()
    print("Tiempo 1 thread   : ",  (end1Thread - start1Thread) * 1000 , "    milisegundos")
    checkValidityOfMatrix()

    start2Threads = time.time()
    concurrentExecute(2)
    end2Threads = time.time()
    print("Tiempo 2 threads  : ", (end2Threads - start2Threads) * 1000 , "    milisegundos")
    checkValidityOfMatrix()

    start4Threads = time.time()
    concurrentExecute(4)
    end4Threads = time.time()
    print("Tiempo 4 threads  : ", (end4Threads - start4Threads) * 1000 , "    milisegundos")
    checkValidityOfMatrix()


if __name__=="__main__":
    main()
