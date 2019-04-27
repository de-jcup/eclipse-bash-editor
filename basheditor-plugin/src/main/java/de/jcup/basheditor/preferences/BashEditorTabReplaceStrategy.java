package de.jcup.basheditor.preferences;

public enum BashEditorTabReplaceStrategy implements PreferenceIdentifiable, PreferenceLabeled{
   
    NEVER("never","Never"),
    
    USE_DEFAULT("use_default","Use eclipse default settings for text editors"),
    
    ALWAYS("always","Always, with custom space amount"),
    ;
    
    private String id;
    private String labelText;

    private BashEditorTabReplaceStrategy(String id, String labelText) {
        this.id = id;
        this.labelText=labelText;
    }

    public String getLabelText() {
        return labelText;
    }
    
    public String getId() {
        return id;
    }

    public static BashEditorTabReplaceStrategy fromId(String strategyId) {
        if (strategyId==null) {
            return getDefault();
        }
        for (BashEditorTabReplaceStrategy strategy: values()) {
            if (strategy.getId().contentEquals(strategyId)) {
                return strategy;
            }
        }
        /* fall back...*/
        return getDefault();
    }

    public static BashEditorTabReplaceStrategy getDefault() {
        return USE_DEFAULT;
    }

   
}
