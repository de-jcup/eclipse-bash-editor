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
package de.jcup.basheditor.callhierarchy;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class BashCallHierarchyContentProvider implements ITreeContentProvider {

    private static final Object[] NONE = new Object[] {};
    private Object[] HOWTO_USE = new Object[] { "To display the bash call hierarchy, select one function or a script and select 'Open Bash Call Hierarchy' menu option." };;

    public IProject projectScope;


    public void setProjectScope(IProject projectScope) {
        this.projectScope = projectScope;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement==null) {
            return HOWTO_USE;
        }
        if (inputElement instanceof BashCallHierarchyRootElement) {
            BashCallHierarchyRootElement entry = (BashCallHierarchyRootElement) inputElement;
            BashCallHierarchyEntry[] entries = entry.getEntries();
            if (entries == null || entries.length == 0) {
                return HOWTO_USE;
            }
            return entries;
        }
        return NONE;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) parentElement;
            return entry.getChildren(projectScope).toArray();
        }
        return NONE;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return entry.getParent();
        }
        return NONE;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return !entry.getChildren(projectScope).isEmpty();
        }
        return false;
    }

}
