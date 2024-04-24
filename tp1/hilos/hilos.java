%%writefile hilos.java

package main;

import java.util.ArrayList;
import java.util.Random;


/**
 * Ejecutar con parametros : n
 */
public class Hilos {

	private static int[][] inicial;
	private static int[][] secuencial;
	private static int[][] concurrente;
	private static int n = 0;
	private static final int ESCALAR = 5;
	private static ArrayList<Thread> threads = new ArrayList<Thread>();


	public static class MatrixThread implements Runnable {

		private int filaDesde;
		private int filaHasta;

		public MatrixThread(int filaDesde,int filaHasta) {
			this.filaDesde = filaDesde;
			this.filaHasta = filaHasta;
		}

		@Override
		public void run() {
			for(int i = filaDesde; i<=filaHasta;i++) {
				for(int j=0;j<n;j++) {
					concurrente[i][j] = inicial[i][j] * ESCALAR;
				}
			}
		}
	}

	public static void main(String[] args) {

		n = Integer.valueOf(args[0]);

		inicializarMatriz(n);
		long secTime = secuentialExecute();
		long concTimeOneThread = concurrentExecute(1);
		checkValidityOfMatrix();
		long concTimeTwoThreads = concurrentExecute(2);
		checkValidityOfMatrix();
		long concTimeFourThreads = concurrentExecute(4);
		checkValidityOfMatrix();

		System.out.println("Tiempo Secuencial : ".concat(String.valueOf(secTime)).concat(" milisegundos"));
		System.out.println("Tiempo 1 Thread   : ".concat(String.valueOf(concTimeOneThread)).concat(" milisegundos"));
		System.out.println("Tiempo 2 Threads  : ".concat(String.valueOf(concTimeTwoThreads)).concat(" milisegundos"));
		System.out.println("Tiempo 4 Threads  : ".concat(String.valueOf(concTimeFourThreads)).concat(" milisegundos"));

	}


	private static void checkValidityOfMatrix() {
		try {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if(secuencial[i][j] != concurrente[i][j]) {
						throw new Exception("resultado erroneo");
					}
				}
			}
			System.out.println("Las matrices secuencial y concurrente son iguales");
		}catch(Exception ex) {
			System.out.println("Las matrices secuencial y concurrente son distintas");
		}
	}


	private static long concurrentExecute(int cantHilos) {
		int filas_por_hilos = n / cantHilos;
		for(int i = 1;i <= cantHilos;i++) {
			int fila_desde = (i - 1) * filas_por_hilos;
			int fila_hasta = (i * filas_por_hilos) -1;
			threads.add(new Thread(new MatrixThread(fila_desde,fila_hasta)));
		}

		long initConcTime = System.currentTimeMillis();
		threads.stream().forEach(t -> t.start());


		threads.stream().forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		long endConcTime = System.currentTimeMillis();
		long concTime = endConcTime - initConcTime;
		threads.clear();
		return concTime;
	}


	private static long secuentialExecute() {
		long initSecTime = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				secuencial[i][j] = inicial[i][j] * ESCALAR;
			}
		}
		long endSecTime = System.currentTimeMillis();
		return endSecTime - initSecTime;
	}


	private static void inicializarMatriz(int n) {
		inicial = new int[n][n];
		concurrente = new int[n][n];
		secuencial = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				inicial[i][j] = new Random().nextInt(9);
			}
		}
	}


}
