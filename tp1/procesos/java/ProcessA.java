import java.io.IOException;

public class ProcessA
{
    public static void main(String[] args)
    {
        try
        {
            long startTime = System.currentTimeMillis();

        	long pid = ProcessHandle.current().pid();
            long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
            System.out.println("PID A -> " + pid + " PPID -> " + parentPid);

            ProcessBuilder pbB = new ProcessBuilder("java", "ProcessB.java");
            pbB.redirectErrorStream(true);
            pbB.inheritIO();
            ProcessBuilder pbC = new ProcessBuilder("java", "ProcessC.java");
            pbC.redirectErrorStream(true);
            pbC.inheritIO();

            ProcessBuilder pbD = new ProcessBuilder("java", "ProcessD.java");
            pbD.redirectErrorStream(true);
            pbD.inheritIO();

            Process pB = pbB.start();
            Process pC = pbC.start();
            Process pD = pbD.start();

            // Esperar a que los procesos B, C, D terminen
            pB.waitFor();
            pC.waitFor();
            pD.waitFor();

            long endTime = System.currentTimeMillis(); // Capture end time

            // Calculate and print the execution time
            long executionTime = endTime - startTime;
            System.out.println("Total execution time of ProcessA: " + executionTime + " milliseconds");
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
