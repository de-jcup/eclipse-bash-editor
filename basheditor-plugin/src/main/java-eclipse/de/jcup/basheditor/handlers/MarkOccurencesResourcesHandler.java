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
package de.jcup.basheditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.basheditor.BashEditorActivator;

public class MarkOccurencesResourcesHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        /*
         * Hint about synchronization with UI: The command menu item is of type "toggle"
         * here we only set the state in a static way to have the information without
         * accessing the UI. Both are starting with "false" and this is the only way to
         * change, so this is okay.
         */
        BashEditorActivator.getDefault().toggleMarkOccurrences();

        return null;
    }

}