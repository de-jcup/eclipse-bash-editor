package de.jcup.basheditor.preferences;

public enum BashEditorLinkFunctionStrategy implements PreferenceIdentifiable, PreferenceLabeled{
   
    /**
     * Lookup only inside script. No external functions searched
     */
    SCRIPT("script","Script only"),
    /**
     * Lookup inside script, when not found, look into project for possible functions
     */
    PROJECT("project","Project scope"),
    /**
     * Lookup inside script, when not found, look into complete workspace for possible functions
     */
    WORKSPACE("workspace","Workspace scope"),
    ;
    
    private String id;
    private String labelText;

    private BashEditorLinkFunctionStrategy(String id, String labelText) {
        this.id = id;
        this.labelText=labelText;
    }

    public String getLabelText() {
        return labelText;
    }
    
    public String getId() {
        return id;
    }

    public static BashEditorLinkFunctionStrategy fromId(String strategyId) {
        if (strategyId==null) {
            return getDefault();
        }
        for (BashEditorLinkFunctionStrategy strategy: values()) {
            if (strategy.getId().contentEquals(strategyId)) {
                return strategy;
            }
        }
        /* fall back...*/
        return getDefault();
    }

    public static BashEditorLinkFunctionStrategy getDefault() {
        return PROJECT;
    }

   
}
