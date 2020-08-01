package de.jcup.basheditor.callhierarchy;

public class BashCallHierarchyRootElement {

    private BashCallHierarchyEntry[] entries;

    public BashCallHierarchyRootElement(BashCallHierarchyEntry ...entries){
        if (entries==null) {
            throw new IllegalArgumentException("Entries may not be null");
        }
        this.entries=entries;
        
        BashCallHierarchyCalculator calculator = new BashCallHierarchyCalculator();
        for (BashCallHierarchyEntry entry: entries) {
            if (entry==null) {
                continue;
            }
            entry.setCalculator(calculator);
        }
    }
    
    public BashCallHierarchyEntry[] getEntries() {
        return entries;
    }
}
