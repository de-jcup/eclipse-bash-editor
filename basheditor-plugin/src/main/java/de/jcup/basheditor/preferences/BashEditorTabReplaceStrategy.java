/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
