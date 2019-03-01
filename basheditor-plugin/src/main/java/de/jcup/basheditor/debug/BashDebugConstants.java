package de.jcup.basheditor.debug;

public class BashDebugConstants {

	public static final String BASH_DEBUG_MODEL_ID = "basheditor.debug.model";
	
	private static final String LAUNCH_CONFIG_PREFIX = "basheditor.debug.launch";

	public static final String LAUNCH_ATTR_BASH_PROGRAM = LAUNCH_CONFIG_PREFIX + ".BASH_PROGRAM";
	public static final String LAUNCH_ATTR_BASH_PARAMS = LAUNCH_CONFIG_PREFIX+".BASH_PARAMS";
	public static final String LAUNCH_ATTR_SOCKET_PORT = LAUNCH_CONFIG_PREFIX + ".DEBUG_PORT";
	public static final String LAUNCH_ATTR_STOP_ON_STARTUP = LAUNCH_CONFIG_PREFIX + ".STOP_ON_START";
	public static final String LAUNCH_ATTR_LAUNCH_MODE = LAUNCH_CONFIG_PREFIX + ".LAUCH_MODE";

	public static final int DEFAULT_DEBUG_PORT = 33333;
	public static final int DEFAULT_DEBUG_PORT_AS_STRING = 33333;

}
