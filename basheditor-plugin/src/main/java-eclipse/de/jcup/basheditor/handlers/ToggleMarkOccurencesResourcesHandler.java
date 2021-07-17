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
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import de.jcup.basheditor.BashEditorActivator;

public class ToggleMarkOccurencesResourcesHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        /* execute toggle */ 
        boolean markOccurrencesAfterToggle = BashEditorActivator.getDefault().toggleMarkOccurrences();

        /*
         * synchronize toggle state to elements - necessary when shortcut ALT+SHIFT+O
         * was pressed
         */
        Command command = event.getCommand();
        boolean currentUIToggleState = getCurrentUIToggleMarkOccurrences(command);
        if (currentUIToggleState!=markOccurrencesAfterToggle) {
            HandlerUtil.toggleCommandState(command);
        }

        return null;
    }

    public static final boolean getCurrentUIToggleMarkOccurrences(Command command) {
        if (command==null) {
            return false;
        }
        State state = command.getState(RegistryToggleState.STATE_ID);
        boolean currentUIToggleState = Boolean.TRUE.equals(state.getValue());
        return currentUIToggleState;
    }

}
