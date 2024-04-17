 public class ProcessC
{
	 public static void main(String[] args)
	 {
	    	long pid = ProcessHandle.current().pid();
	        long parentPid = ProcessHandle.current().parent().map(ProcessHandle::pid).orElse(-1L);
	        System.out.println("PID C -> " + pid + " PPID -> " + parentPid);
	 }
}
