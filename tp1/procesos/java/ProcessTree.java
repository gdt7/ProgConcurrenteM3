import java.io.IOException;

public class ProcessTree
{
    public static void main(String[] args)
    {
        try
        {

            // Ejecutar el proceso A
            ProcessBuilder pbA = new ProcessBuilder("java", "ProcessA.java");
            pbA.redirectErrorStream(true);
            pbA.inheritIO();
            Process pA = pbA.start();

            pA.waitFor();


        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
