package de.jcup.basheditor.preferences;

import static org.junit.Assert.*;

import org.junit.Test;

public class BashEditorTabReplaceStrategyTest {

    @Test
    public void fallback_check() {
        assertEquals(BashEditorTabReplaceStrategy.USE_DEFAULT,BashEditorTabReplaceStrategy.fromId(null));
        assertEquals(BashEditorTabReplaceStrategy.USE_DEFAULT,BashEditorTabReplaceStrategy.fromId(""));
        assertEquals(BashEditorTabReplaceStrategy.USE_DEFAULT,BashEditorTabReplaceStrategy.fromId("xxx"));
    }
    
    @Test
    public void dedicated_check() {
        assertEquals(BashEditorTabReplaceStrategy.USE_DEFAULT,BashEditorTabReplaceStrategy.fromId("default"));
        assertEquals(BashEditorTabReplaceStrategy.NEVER,BashEditorTabReplaceStrategy.fromId("never"));
        assertEquals(BashEditorTabReplaceStrategy.ALWAYS,BashEditorTabReplaceStrategy.fromId("always"));
    }

}
