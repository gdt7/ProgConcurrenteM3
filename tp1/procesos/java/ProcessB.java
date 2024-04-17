import java.io.IOException;

public class ProcessB
 {
	public static void main(String[] args)
   {
        try
         {

        	long pid = ProcessHandle.current().pid();
            long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
            System.out.println("PID B -> " + pid + " PPID -> " + parentPid);

            // Crear los procesos E, F
            ProcessBuilder pbE = new ProcessBuilder("java", "ProcessE.java");
            pbE.redirectErrorStream(true);
            pbE.inheritIO();

            ProcessBuilder pbF = new ProcessBuilder("java", "ProcessF.java");
            pbF.redirectErrorStream(true);
            pbF.inheritIO();

            Process pE = pbE.start();
            Process pF = pbF.start();

            // Esperar a que los procesos E, F terminen
            pE.waitFor();
            pF.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
