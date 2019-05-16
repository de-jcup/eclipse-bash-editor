package de.jcup.basheditor.debug.launch;

public enum TerminalCommandVariable {

    CMD_TITLE("BE_CMD_TITLE"),
    CMD_CALL("BE_CMD_CALL");
    
    private String id;

    private TerminalCommandVariable(String id) {
        this.id=id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getVariableRepresentation() {
        return "${"+id+"}";
    }
}
