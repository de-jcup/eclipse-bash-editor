package de.jcup.basheditor.callhierarchy;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class BashCallHierarchyContentProvider implements ITreeContentProvider {

    private static final Object[] NONE = new Object[] {};

    public IProject projectScope;

    public void setProjectScope(IProject projectScope) {
        this.projectScope = projectScope;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof BashCallHierarchyRootElement) {
            BashCallHierarchyRootElement entry = (BashCallHierarchyRootElement) inputElement;
            return entry.getEntries();
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
