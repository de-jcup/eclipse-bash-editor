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
package de.jcup.basheditor.script;

import static org.junit.Assert.*;

public class AssertVariable{

    private BashVariable variable;

    AssertVariable(BashVariable variable) {
        this.variable=variable; 
    }

    public AssertVariable withValue(String value) {
        assertEquals("Variable has not expected content", value,variable.getInitialValue());
        return this;
    }
    public AssertVariable hasAssignments(int amount) {
        assertEquals("Variable has not expected assignments",amount,variable.getAssignments().size());
        return this;
    }

    public AssertVariable islocal() {
        assertTrue("Is not local!",variable.isLocal());
        return this;
    }
    
    public AssertVariable isGlobal() {
        assertFalse("Is not global!",variable.isLocal());
        return this;
    }
    
}