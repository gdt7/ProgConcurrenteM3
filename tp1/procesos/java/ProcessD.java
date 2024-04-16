import java.io.IOException;

public class ProcessD
{
	 public static void main(String[] args)
	 {
	        try
					{
	        	long pid = ProcessHandle.current().pid();
	            // Obtener el PID del proceso padre
	            long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
	            System.out.println("PID D -> " + pid + " PPID -> " + parentPid);

	            // Crear el proceso G
	            ProcessBuilder pbG = new ProcessBuilder("java", "ProcessG.java");
	            pbG.redirectErrorStream(true);
	            pbG.inheritIO();
	            Process pG = pbG.start();
	            // Esperar a que el proceso G termine
	            pG.waitFor();
	        }
					catch (IOException | InterruptedException e)
					{
	            e.printStackTrace();
	        }
	    }
}
