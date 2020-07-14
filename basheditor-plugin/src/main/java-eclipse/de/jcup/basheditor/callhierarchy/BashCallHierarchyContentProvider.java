package de.jcup.basheditor.callhierarchy;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class BashCallHierarchyContentProvider implements ITreeContentProvider{

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof BashCallHierarchyModel) {
            BashCallHierarchyModel bchm = (BashCallHierarchyModel) inputElement;
            return bchm.getRootElements().toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) parentElement;
            return entry.getChildren().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return entry.getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return ! entry.getChildren().isEmpty();
        }
        return false;
    }

}
