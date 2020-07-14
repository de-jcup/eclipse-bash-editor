package de.jcup.basheditor.callhierarchy;

import java.util.ArrayList;
import java.util.List;

public class BashCallHierarchyEntry {

    List<BashCallHierarchyEntry> children = new ArrayList<>();
    BashCallHierarchyEntry parent;

    public List<BashCallHierarchyEntry> getChildren(){
        return children;
    }

    public BashCallHierarchyEntry getParent() {
        return parent;
    }
}
