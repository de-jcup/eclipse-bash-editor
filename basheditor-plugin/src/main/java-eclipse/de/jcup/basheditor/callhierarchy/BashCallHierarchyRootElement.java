package de.jcup.basheditor.callhierarchy;

public class BashCallHierarchyRootElement {

    private BashCallHierarchyEntry[] entries;

    public BashCallHierarchyRootElement(BashCallHierarchyEntry ...entries){
        this.entries=entries;
    }
    
    public BashCallHierarchyEntry[] getEntries() {
        return entries;
    }
}
