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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class CommandStringVariableReplaceSupportTest {
    private CommandStringVariableReplaceSupport supportToTest;
    
    @Before
    public void before() {
        supportToTest=new CommandStringVariableReplaceSupport();
    }
    
    @Test
    public void real_life_example() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("BE_CMD_CALL", "cd /tmp;./xyz4722810921592969191.txt -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";");
        map.put("BE_CMD_TITLE", "Bash Editor DEBUG Session:xyz4722810921592969191.txt");
        /* execute */
        String result = supportToTest.replaceVariables("bash -c x-terminal-emulator -e bash --login -c '${BE_CMD_CALL}' &", map);
        
        /* test*/
        assertEquals("bash -c x-terminal-emulator -e bash --login -c 'cd /tmp;./xyz4722810921592969191.txt -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";' &",result);
    }
    @Test
    public void variables_are_replaced_unkonwn_is_kept2() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("xxx", "123456");
        map.put("yyy", "abcdefghijk");
        /* execute */
        String result = supportToTest.replaceVariables("a '${xxx}' with ${yyy} not ${zzz} a", map);
        
        /* test*/
        assertEquals("a '123456' with abcdefghijk not ${zzz} a",result);
    }
    
    @Test
    public void variables_are_replaced() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("xxx", "123456");
        map.put("yyy", "abcdefghijk");
        /* execute */
        String result = supportToTest.replaceVariables("a '${xxx}' with ${yyy}", map);
        
        /* test*/
        assertEquals("a '123456' with abcdefghijk",result);
    }
    
    @Test
    public void variables_are_replaced2() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("xxx", "123456");
        map.put("yyy", "abcdefghijk");
        /* execute */
        String result = supportToTest.replaceVariables("a '${xxx}' with ${yyy},", map);
        
        /* test*/
        assertEquals("a '123456' with abcdefghijk,",result);
    }
    
    @Test
    public void variables_are_replaced_unkonwn_is_kept() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("xxx", "123456");
        map.put("yyy", "abcdefghijk");
        /* execute */
        String result = supportToTest.replaceVariables("a '${xxx}' with ${yyy} not ${zzz}", map);
        
        /* test*/
        assertEquals("a '123456' with abcdefghijk not ${zzz}",result);
    }
    
    @Test
    public void variables_are_replaced_null_is_shown_as_null() {
        /* prepare*/
        Map<String, String> map = new HashMap<>();
        map.put("xxx", "123456");
        map.put("yyy", "abcdefghijk");
        map.put("zzz", "null");
        /* execute */
        String result = supportToTest.replaceVariables("a '${xxx}' with ${yyy} null: ${zzz}", map);
        
        /* test*/
        assertEquals("a '123456' with abcdefghijk null: null",result);
    }

}
