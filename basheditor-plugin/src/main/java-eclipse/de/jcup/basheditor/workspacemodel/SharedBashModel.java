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
package de.jcup.basheditor.workspacemodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.callhierarchy.BashCallHierarchyEntry;
import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.script.BashScriptModel;

/**
 * This is ONE model for all. Means it will contain information for any
 * 
 * @author albert
 *
 */
public class SharedBashModel {
    private Map<IResource, BashScriptModel> sharedMap = new HashMap<IResource, BashScriptModel>();

    public void update(IResource resource, BashScriptModel scriptModel) {
        sharedMap.put(resource, scriptModel);
    }

    public void remove(IResource resource) {
        sharedMap.remove(resource);
    }

    public List<SharedModelMethodTarget> findResourcesHavingMethods(String functionName, IProject projectScope) {
        List<SharedModelMethodTarget> list = new ArrayList<SharedModelMethodTarget>();
        if (functionName == null) {
            return list;
        }
        Set<IResource> resources = sharedMap.keySet();
        for (IResource resource : resources) {
            if (projectScope != null) {
                boolean notInProjectScope = !projectScope.equals(resource.getProject());
                if (notInProjectScope) {
                    continue;
                }
            }
            BashScriptModel scriptModel = sharedMap.get(resource);
            if (scriptModel == null) {
                continue;
            }
            addFirstFunctionFoundInResource(list, functionName, resource, scriptModel);
        }
        return list;
    }

    private void addFirstFunctionFoundInResource(List<SharedModelMethodTarget> targets, String functionName, IResource resource, BashScriptModel scriptModel) {
        Collection<BashFunction> functions = scriptModel.getFunctions();
        if (functions == null || functions.isEmpty()) {
            return;
        }
        if ("feature-129-lib1.sh".contentEquals(resource.getName())) {
            System.out.println("got resource:" + resource);
        }
        for (BashFunction function : functions) {
            if (functionName.contentEquals(function.getName())) {
                SharedModelMethodTarget target = new SharedModelMethodTarget(resource, function);
                targets.add(target);
                /* we break on first function found here */
                break;
            }
        }
    }

    public Collection<? extends BashCallHierarchyEntry> findResourcesContainingText(BashCallHierarchyEntry parent, String text, IProject projectScope) {
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        Set<IResource> resources = sharedMap.keySet();
        for (IResource resource : resources) {
            if (projectScope != null) {
                boolean notInProjectScope = !projectScope.equals(resource.getProject());
                if (notInProjectScope) {
                    continue;
                }
            }
            if (resource instanceof IFile) {
                BashCallHierarchyEntry rootEntry = parent.getRootEntry();
                IFile file = (IFile) resource;
                int pos = 0;
                int lineNumber = 1;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()))) {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        int column = line.indexOf(text);
                        if (column != -1) {
                            BashCallHierarchyEntry entry = new BashCallHierarchyEntry(parent);
                            entry.setResource(file);
                            BashScriptModel scriptModel = sharedMap.get(resource);
                            if (scriptModel != null) {
                                for (BashFunction function : scriptModel.getFunctions()) {
                                    int start = function.getPosition();
                                    
                                    int end = function.getEnd();

                                    if (pos >= start && pos <= end) {
                                        entry.setElement(function);
                                    }
                                }
                            }
                            if (entry.getElement() == null) {
                                entry.setElement("script");
                            }
                            entry.setColumn(column);
                            entry.setLine(lineNumber);
                            entry.setPos(pos);
                            
                            if (!rootEntry.getResource().equals(file) || rootEntry.getPos() != pos) {
                                entries.add(entry);
                            }
                        }
                        pos += line.length() + 1;
                        lineNumber++;

                    }

                } catch (IOException | CoreException e) {
                    BashEditorUtil.logError("Was not able to read file contents:" + file, e);
                }
            }
        }
        return entries;
    }

}
