package de.jcup.basheditor.debug.launch;

public class OSUtil {
	
	private static boolean isWindows;
	
	static {
		String osName = System.getProperty("os.name");
		isWindows = osName.toLowerCase().indexOf("windows")!=-1;
	}
	
	public static boolean isWindows() {
		return isWindows;
	}
}
