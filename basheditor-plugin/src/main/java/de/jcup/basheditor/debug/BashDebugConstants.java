/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.basheditor.debug;

public class BashDebugConstants {

	public static final String BASH_DEBUG_MODEL_ID = "basheditor.debug.model";
	
	private static final String LAUNCH_CONFIG_PREFIX = "basheditor.debug.launch";

	public static final String LAUNCH_ATTR_BASH_PROGRAM = LAUNCH_CONFIG_PREFIX + ".BASH_PROGRAM";
	public static final String LAUNCH_ATTR_BASH_PARAMS = LAUNCH_CONFIG_PREFIX+".BASH_PARAMS";
	public static final String LAUNCH_ATTR_SOCKET_PORT = LAUNCH_CONFIG_PREFIX + ".DEBUG_PORT";
	public static final String LAUNCH_ATTR_STOP_ON_STARTUP = LAUNCH_CONFIG_PREFIX + ".STOP_ON_START";
	public static final String LAUNCH_ATTR_LAUNCH_MODE = LAUNCH_CONFIG_PREFIX + ".LAUCH_MODE";
	
	public static final String LAUNCH_ENVIRONMENT_PROPERTIES = LAUNCH_CONFIG_PREFIX + ".ENVIRONMENT";

	public static final int DEFAULT_DEBUG_PORT = 33333;

}
