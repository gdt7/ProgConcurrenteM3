import java.io.IOException;

public class ProcessF
{
	 public static void main(String[] args)
	  {
	        try
					{
	        	long pid = ProcessHandle.current().pid();
	            // Obtener el PID del proceso padre
	            long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
	            System.out.println("PID F -> " + pid + " PPID -> " + parentPid);

	            // Crear los procesos H, I
	            ProcessBuilder pbH = new ProcessBuilder("java", "ProcessH.java");
	            pbH.redirectErrorStream(true);
	            pbH.inheritIO();
	            ProcessBuilder pbI = new ProcessBuilder("java", "ProcessI.java");
	            pbI.redirectErrorStream(true);
	            pbI.inheritIO();

	            Process pH = pbH.start();
	            Process pI = pbI.start();

	            // Esperar a que los procesos H, I terminen
	            pH.waitFor();
	            pI.waitFor();
	        }
					catch (IOException | InterruptedException e)
				  {
	            e.printStackTrace();
	        }
	    }
}
