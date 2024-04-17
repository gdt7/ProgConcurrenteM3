import java.io.IOException;

public class ProcessI
{
	 public static void main(String[] args)
	 {
	        try
					{
	        	long pid = ProcessHandle.current().pid();
	            // Obtener el PID del proceso padre
	            long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
	            System.out.println("PID I -> " + pid + " PPID -> " + parentPid);


	            // Crear el proceso J
	            ProcessBuilder pbJ = new ProcessBuilder("java", "ProcessJ.java");
	            pbJ.redirectErrorStream(true);
	            pbJ.inheritIO();
	            Process pJ = pbJ.start();
	            // Esperar a que el proceso J termine
	            pJ.waitFor();
	        }
					catch (IOException | InterruptedException e)
					 {
	            e.printStackTrace();
	        }
	    }
}
