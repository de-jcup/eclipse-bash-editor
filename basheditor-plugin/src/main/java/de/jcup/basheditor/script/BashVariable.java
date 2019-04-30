package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.List;

public class BashVariable {

    private String name;
    private String initialValue;
    private List<BashVariableAssignment> assignments = new ArrayList<>();
    private boolean local;

    public BashVariable(String name, BashVariableAssignment assignment) {
        this.name = name;
        assignments.add(assignment);// initial on first pos
    }

    public void setInitialValue(String value) {
        this.initialValue = value;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public BashVariableAssignment getInitialAssignment() {
        return assignments.iterator().next();
    }

    /**
     * Get the assignments in ordered way as defined inside script! So first entry
     * here is also first (inital) assignment
     * 
     * @return
     */
    public List<BashVariableAssignment> getAssignments() {
        return assignments;
    }

    public String getName() {
        return name;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isLocal() {
        return local;
    }

}
