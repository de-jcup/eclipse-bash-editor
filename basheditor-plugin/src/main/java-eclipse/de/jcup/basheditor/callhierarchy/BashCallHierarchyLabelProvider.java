package de.jcup.basheditor.callhierarchy;

import org.eclipse.jface.viewers.LabelProvider;

public class BashCallHierarchyLabelProvider extends LabelProvider{

    
    @Override
    public String getText(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            return entry.type+":"+entry.name+" - resource:"+entry.resource;
        }
        return super.getText(element);
    }
}
