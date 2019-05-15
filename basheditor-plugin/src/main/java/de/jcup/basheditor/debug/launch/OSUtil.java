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
package de.jcup.basheditor.debug.launch;

import java.util.regex.Pattern;

public class OSUtil {
    private static final Pattern WINDOWS_BACKSLASH_TO_SLASH_PATTERN = Pattern.compile("\\\\");
	private static boolean isWindows;
    private static boolean isMac;
	
	static {
		String osName = System.getProperty("os.name");
		isWindows = osName.toLowerCase().indexOf("windows")!=-1;
		isMac=osName.toLowerCase().indexOf("mac")!=-1;
	}
	
	public static boolean isWindows() {
		return isWindows;
	}

    public static String toUnixPath(String path) {
        if (path==null) {
            return "null";
        }
        // e.g. "C:\\Users\\albert\\.basheditor\\remote-debugging-v1.sh"
        int index = path.indexOf(':');
        if (index!=1) {
            return path;
        }
        char windowsDrive = path.charAt(0);
        String remaining = path.substring(2);
        StringBuilder sb = new StringBuilder();
        sb.append('/');
        sb.append(windowsDrive);
        sb.append(WINDOWS_BACKSLASH_TO_SLASH_PATTERN.matcher(remaining).replaceAll("/"));
        
        return sb.toString();
    }

    public static boolean isMacOS() {
        return isMac;
    }
}
