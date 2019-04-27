package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.List;

public class BashVariable {

    private String initialValue;
    private List<BashVariableAssignment> assignments = new ArrayList<>();

    public void setInitialValue(String value) {
        this.initialValue = value;
    }

    public String getInitialValue() {
        return initialValue;
    }
    
    /**
     * Get the assignments in ordered way as defined inside script! So 
     * first entry here is also first (inital) assignment
     * @return
     */
    public List<BashVariableAssignment> getAssignments() {
        return assignments;
    }

}
