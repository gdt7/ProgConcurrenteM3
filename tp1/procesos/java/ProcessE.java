public class ProcessE
 {
	 public static void main(String[] args)
	 {
	    	long pid = ProcessHandle.current().pid();
	        // Obtener el PID del proceso padre
	        long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
	        System.out.println("PID E -> " + pid + " PPID -> " + parentPid);

	 }
}
