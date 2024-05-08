package paquete;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class CruceGranjero3 {

    private static final Semaphore semaphore1 = new Semaphore(1); // Inicialmente solo un hilo puede adquirir el semáforo

    private static ArrayList<String> arrayListA = new ArrayList<>();
    private static ArrayList<String> arrayListB = new ArrayList<>();

    // Métodos para simular el cruce de los elementos
    private static  void cruzar(String pasajero) {
        System.out.println("Cruzando de punto A hacia B...");
        System.out.println("El " + pasajero + " se encuentran en el punto B.");

        arrayListB.add("granjero");
        arrayListA.remove("granjero");

        if (!pasajero.equals("granjero")) {
        	arrayListB.add(pasajero);
            arrayListA.remove(pasajero);
		}
    }


    private static  void regresar(String pasajero) {
        System.out.println("Regresando de punto B hacia A...");
        System.out.println("El " + pasajero + " ha cruzado de regreso al punto A.");

        arrayListA.add(pasajero);
        arrayListB.remove(pasajero); // Eliminar el elemento "zorro" del ArrayList

        if (!pasajero.equals("granjero")) {

            arrayListA.add("granjero");
            arrayListB.remove("granjero");
		}
    }


    // Método principal
    public static void main(String[] args) {

        Thread hilo1 = new Thread(() -> {
        		try {
					semaphore1.acquire();
					cruce();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					semaphore1.release();
				}

        });

        Thread hilo2 = new Thread(() -> {
        	try {
				semaphore1.acquire();
				cruce();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				semaphore1.release();
			}


        });

        hilo1.start();
        hilo2.start();


        try {
            hilo1.join();
            hilo2.join();
            System.out.println("Hilos finalizado OK");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Fin Exitoso!");

    }
	private static void cruce() {


    	arrayListA.add("granjero");
        arrayListA.add("zorro");
        arrayListA.add("pollo");
        arrayListA.add("maiz");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Se encuentran en el punto A: " + arrayListA);
    	System.out.println("Se encuentran en el punto B: " + arrayListB);

    	boolean cruzado = true;
        while (true) {

        	String opcion;
			// Solicitar al usuario que ingrese una opción válida
        	if(cruzado) {
        		System.out.println("Con que pasajero desea cruzar: ");
        	}else {
        		System.out.println("Con que pasajero desea regresar: ");
        	}

        	 	do {
			            //System.out.print("¿Cruzar/Regresar con? (Zorro, Pollo, Maiz, Salir): ");
			            opcion = scanner.nextLine().toLowerCase();

			            if (opcion.equals("zorro") || opcion.equals("pollo") || opcion.equals("maiz") || opcion.equals("salir") || opcion.equals("granjero")) {
			            	if (cruzado) {
								if (!opcion.equals("salir") && !arrayListA.contains(opcion)) {
									System.out.println("No se encuentra en el punto A!. Por favor, ingresa una opción válida");
								}else {
									break;
								}
								//break;
							}else {
								if (!opcion.equals("salir") && !arrayListB.contains(opcion)) {
									System.out.println("No se encuentra en el punto B!. Por favor, ingresa una opción válida");
								}else {
									break;
								}
							}

			            } else {
			                System.out.println("Opción inválida. Por favor, ingresa una opción válida (Zorro, Pollo, Maiz, Salir): ");
			            }
        	 		} while (true);

            //String opcion = scanner.nextLine().toLowerCase();

            if (opcion.equals("salir")) {
                System.out.println("Fin del programa.");
                break;
            }

            try {

                // Realizar el cruce según la opción ingresada por el usuario
                if(cruzado) {
            		cruzar(opcion);
            		cruzado = false;
            	}else {
            		regresar(opcion);
            		cruzado = true;
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }

            validarPasajeros();

            System.out.println("Se encuentran en el punto A: " + arrayListA);
        	System.out.println("Se encuentran en el punto B: " + arrayListB);
        	}


        	scanner.close();


	}


	private static void validarPasajeros() {

			if (arrayListA.contains("zorro") && arrayListA.contains("pollo") && !arrayListA.contains("granjero")){
				 System.out.println("El zorro se come al pollo en el punto A!. \nSe finaliza el programa.");
				 System.exit(0); // Terminar el programa

			}else if (arrayListA.contains("pollo") && arrayListA.contains("maiz")&& !arrayListA.contains("granjero")) {
				 System.out.println("El pollo se come el maiz en el punto A!. \nSe finaliza el programa.");
		         System.exit(0); // Terminar el programa
		    }

			if (arrayListB.contains("zorro") && arrayListB.contains("pollo") && !arrayListB.contains("granjero")){
				 System.out.println("El zorro se come al pollo en el punto B!. \nSe finaliza el programa.");
				 System.exit(0); // Terminar el programa
			}else if((arrayListB.contains("pollo") && arrayListB.contains("maiz") && !arrayListB.contains("granjero"))) {
				 System.out.println("El pollo se come el maiz en el punto B!. \nSe finaliza el programa.");
		         System.exit(0); // Terminar el programa
		    }

			 // Verificar si arrayList1 está vacío
		    if (arrayListA.isEmpty()) {
		        System.out.println("Todos estan en el punto B. Felicitaciones!");
		        System.exit(0); // Terminar el programa
	    	}


}
}
/*
 *  1-Llevar al pollo al punto B.
	2-Regresar solo al punto A.
	3-Llevar al zorro al punto B.
	4-Regresar con el pollo al punto A.
	5-Dejar al pollo en el punto A y llevar al maíz al punto B.
	6-Regresar solo al punto A.
 *	7-Llevar al pollo al punto B.
*/
