package de.jcup.basheditor.callhierarchy;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class BashCallHierarchyContentProvider implements ITreeContentProvider{

    private static final Object[] NONE = new Object[] {};
    
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof BashCallHierarchyModel) {
            BashCallHierarchyModel bchm = (BashCallHierarchyModel) inputElement;
            Object[] array = bchm.getRootElements().toArray();
            return array;
        }
        return NONE ;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof BashCallHierarchyModel) {
            BashCallHierarchyModel bchm = (BashCallHierarchyModel) parentElement;
            return bchm.getRootElements().toArray();
        }
        if (parentElement instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) parentElement;
            return entry.getChildren().toArray();
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
        if (element instanceof BashCallHierarchyModel) {
            BashCallHierarchyModel bchm = (BashCallHierarchyModel) element;
            return ! bchm.getRootElements().isEmpty();
        }
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return ! entry.getChildren().isEmpty();
        }
        return false;
    }

}
