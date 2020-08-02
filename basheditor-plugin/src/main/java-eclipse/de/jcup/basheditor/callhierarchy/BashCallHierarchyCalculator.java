package de.jcup.basheditor.callhierarchy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.outline.ItemType;
import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.basheditor.workspacemodel.SharedBashModel;

public class BashCallHierarchyCalculator {

    private SharedBashModel model;

    public BashCallHierarchyCalculator() {
        this.model = BashEditorActivator.getDefault().getModel();
    }

    public List<BashCallHierarchyEntry> findBashFunctionCallers(BashCallHierarchyEntry parent, BashFunction function, IProject projectScope) {
        if (model == null || function == null) {
            return Collections.emptyList();
        }

        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        entries.addAll(findResourcesContainingText(parent, function.getName(), projectScope));
        return entries;

    }

    public List<BashCallHierarchyEntry> findChildren(BashCallHierarchyEntry entry, IProject projectScope) {
        if (model == null || entry == null || entry.getElement() == null) {
            return Collections.emptyList();
        }
        Object element = entry.getElement();

        /* when item we search for bash function */
        if (element instanceof Item) {
            Item item = (Item) element;
            if (item.getItemType() != ItemType.FUNCTION) {
                return Collections.emptyList();
            }
            BashScriptModel m = model.getModel(entry.getResource());
            BashFunction function = m.findBashFunctionByName(item.getName());
            if (function == null) {
                return Collections.emptyList();
            }
            return findBashFunctionCallers(entry, function, projectScope);
        }

        if (element instanceof BashFunction) {
            return findBashFunctionCallers(entry, (BashFunction) element, projectScope);
        }
        if (element instanceof IResource) {
            return findCallersAndIncludes(entry, (IResource) element, projectScope);
        }
        /* when other */
        List<BashCallHierarchyEntry> entries = new ArrayList<>();

        return entries;
    }

    private List<BashCallHierarchyEntry> findCallersAndIncludes(BashCallHierarchyEntry parent, IResource element, IProject projectScope) {
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        entries.addAll(findResourcesContainingText(parent, element.getName(), projectScope));
        return entries;
    }

    /**
     * Find all resources containing given text and return them as hierarchy entry.
     * Functions will be treated in special way.
     * 
     * @param parent
     * @param text
     * @param projectScope
     * @return
     */
    public Collection<? extends BashCallHierarchyEntry> findResourcesContainingText(BashCallHierarchyEntry parent, String text, IProject projectScope) {
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        Iterator<IResource> it = model.getResourceIterator();
        while (it.hasNext()) {
            IResource resource = it.next();
            if (!(resource instanceof IFile)) {
                continue;
            }
            if (projectScope != null) {
                boolean notInProjectScope = !projectScope.equals(resource.getProject());
                if (notInProjectScope) {
                    continue;
                }
            }
            IFile file = (IFile) resource;
            addHierarchyEntriesForResource(file, parent, text, projectScope, entries);
        }
        return entries;
    }

    private void addHierarchyEntriesForResource(IFile file, BashCallHierarchyEntry parent, String text, IProject projectScope, List<BashCallHierarchyEntry> entries) {

        BashCallHierarchyEntry rootEntry = parent.getRootEntry();
        int offset = 0;
        int lineNumber = 1;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                int column = line.indexOf(text);
                if (column != -1) {
                    handleTextFoundInLine(text, file, parent, entries, rootEntry, offset, lineNumber, column);
                }
                offset += line.length() + 1;
                lineNumber++;

            }

        } catch (IOException | CoreException e) {
            BashEditorUtil.logError("Was not able to read file contents:" + file, e);
        }
    }

    private void handleTextFoundInLine(String text, IFile file, BashCallHierarchyEntry parent, List<BashCallHierarchyEntry> entries, BashCallHierarchyEntry rootEntry, int offset, int lineNumber,
            int column) {
        /* either we have a function here, or its a text call */
        BashScriptModel scriptModel = model.getModel(file);
        if (scriptModel == null) {
            return;
        }
        BashCallHierarchyEntry entry = createEntry(file, parent);
        entry.setOffset(offset);
        entry.setColumn(column);
        entry.setLine(lineNumber);

        FunctionCheckData data = checkCallFromFunction(text, entry, offset, scriptModel);
        if (data.isFunctionItself) {
            return;
        }
        if (!data.isCalledFromFunction) {
            /* normal text, no function */
            entry.setElement(file);
            entry.setLength(text.length());
        }

        entries.add(entry);
    }

    private FunctionCheckData checkCallFromFunction(String text, BashCallHierarchyEntry entry, int offset, BashScriptModel scriptModel) {
        FunctionCheckData data = null;
        /*
         * check if found text position is inside a function - means this this entry is
         * for a function
         */
        for (BashFunction function : scriptModel.getFunctions()) {

            data = checkCallFromFunction(text, offset, entry, function);

            if (data.isFunctionItself || data.isCalledFromFunction) {
                break;
            }
        }
        if (data == null) {
            data = new FunctionCheckData();
        }
        return data;
    }

    private FunctionCheckData checkCallFromFunction(String text, int offsetInFileWhereTextIsFound, BashCallHierarchyEntry entry, BashFunction function) {
        FunctionCheckData data = new FunctionCheckData();
        int functionOffset = function.getPosition();
        int functionOffsetEnd = function.getEnd();

        if (function.getName().contentEquals(text)) {
            /* same function name as caller */
            IResource resource = entry.getParent().getResource();
            if (entry.getResource().equals(resource)) {
                /* same resource */
                if (offsetInFileWhereTextIsFound == functionOffset) {
                    /* function definition itself - so false positive */
                    data.isFunctionItself=true;
                    return data;
                } else {
                    /* not same offset, so inside this function it calls itself */
                    data.rescursion = true;
                }
            } else {
                /*
                 * not same resource - but same function name. So this is a false positive,
                 * because the function would always do a recursive call to itself!
                 */
                data.isFunctionItself=true;
                return data;
            }

        } else {
            /* maybe other function found - where this text was found */
        }
        /* check offset belongs to a function part */
        boolean insideFunction = offsetInFileWhereTextIsFound >= functionOffset && offsetInFileWhereTextIsFound <= functionOffsetEnd;
        if (!insideFunction) {
            return data;
        }
        entry.setElement(function);
        entry.setLength(function.getName().length());
        if (data.rescursion) {
            entry.setRecursion(true);
        }
        data.isCalledFromFunction = true;
        return data;
    }

    private class FunctionCheckData {
        boolean rescursion;
        boolean isFunctionItself;
        boolean isCalledFromFunction;
    }

    private BashCallHierarchyEntry createEntry(IFile file, BashCallHierarchyEntry parent) {
        BashCallHierarchyEntry entry = new BashCallHierarchyEntry(parent);
        entry.setResource(file);
        return entry;
    }

}
