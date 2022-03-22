/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.basheditor;

import org.eclipse.core.expressions.PropertyTester;

import de.jcup.basheditor.preferences.BashEditorPreferences;

public class OpenPathInTerminalPropertyTester extends PropertyTester {

    public static final String PROPERTY_NAMESPACE = "de.jcup.basheditor";
    public static final String PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION = "isOpenPathActionEnabled";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
	    if (PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION.equals(property)){
	        return BashEditorPreferences.getInstance().isOpenPathInTerminalEnabled();
        }
	    return false;
    }

}