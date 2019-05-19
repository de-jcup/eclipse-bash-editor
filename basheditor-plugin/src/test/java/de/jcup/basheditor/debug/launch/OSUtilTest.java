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

import static org.junit.Assert.*;

import org.junit.Test;

public class OSUtilTest {

    @Test
    public void bugfix_139_convert_unixpathes_in_windows() throws Exception {
        
        /* windows pathes are adopted to unix pathes in min-gw style*/
        assertEquals("/C/Users/albert/.basheditor/remote-debugging-v1.sh", OSUtil.toUnixPath("C:\\Users\\albert\\.basheditor\\remote-debugging-v1.sh"));
        assertEquals("/D/some/Other/.path/xYz.sh", OSUtil.toUnixPath("D:\\some\\Other\\.path\\xYz.sh"));
        assertEquals("/X", OSUtil.toUnixPath("X:"));
        assertEquals("/Y/file1.txt", OSUtil.toUnixPath("Y:\\file1.txt"));
        
        /* unix pathes keep as is */
        assertEquals("/C/Users/albert/.basheditor/remote-debugging-v1.sh", OSUtil.toUnixPath("/C/Users/albert/.basheditor/remote-debugging-v1.sh"));
        assertEquals("/D/some/Other/.path/xYz.sh", OSUtil.toUnixPath("/D/some/Other/.path/xYz.sh"));
        assertEquals("/X", OSUtil.toUnixPath("/X"));
        assertEquals("/Y/file1.txt", OSUtil.toUnixPath("/Y/file1.txt"));
        assertEquals("/file1.txt", OSUtil.toUnixPath("/file1.txt"));
        
    }
}
