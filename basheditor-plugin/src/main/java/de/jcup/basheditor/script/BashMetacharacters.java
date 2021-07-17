package de.jcup.basheditor.script;

public class BashMetacharacters {

    public static char[] METACHARACTERS_WITHOUT_WHITESPACES = new char[] { 
            
            '|', '&', ';',

            '(', ')',

            '<', '>',

    };

    public static boolean isMetaCharacter(char character) {
        boolean isMetaCharacter = false;
        for (char c: METACHARACTERS_WITHOUT_WHITESPACES) {
            isMetaCharacter = isMetaCharacter || character == c;
        }
        isMetaCharacter = isMetaCharacter || Character.isWhitespace(character);

        return isMetaCharacter;
    }
}
