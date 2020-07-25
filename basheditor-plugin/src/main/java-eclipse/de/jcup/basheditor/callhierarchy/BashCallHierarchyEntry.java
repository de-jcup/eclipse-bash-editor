package de.jcup.basheditor.callhierarchy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

public class BashCallHierarchyEntry {

    IResource resource;
    String name;
    List<BashCallHierarchyEntry> children = new ArrayList<>();
    BashCallHierarchyEntry parent;
    BashCallType type;

    public List<BashCallHierarchyEntry> getChildren(){
        return children;
    }

    public BashCallHierarchyEntry getParent() {
        return parent;
    }
}
