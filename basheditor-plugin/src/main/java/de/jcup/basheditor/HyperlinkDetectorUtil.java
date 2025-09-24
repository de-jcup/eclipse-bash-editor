/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.basheditor;

public class HyperlinkDetectorUtil {

    public static HoveredTextInfo createHoveredTextInfo(String leftChars, String rightChars, int startOffset) {
        HoveredTextInfo info = new HoveredTextInfo(startOffset);
        StringBuilder sbFunctionName = new StringBuilder();
        StringBuilder sbFileName = new StringBuilder();

        char[] left = leftChars.toCharArray();
        // file paths
        for (int i = left.length - 1; i >= 0; i--) {
            char c = left[i];
            if (!isAllowedFilePathCharacter(c)) {
                break;
            }
            info.filenameOffsetLeft--;
            sbFileName.insert(0, c);
        }

        for (char c : rightChars.toCharArray()) {
            if (isAllowedFilePathCharacter(c)) {
                sbFileName.append(c);
            }
        }
        // functions
        for (int i = left.length - 1; i >= 0; i--) {
            char c = left[i];
            if (!isAllowedBashFunctionCharacter(c)) {
                break;
            }
            info.functionOffsetLeft--;
            sbFunctionName.insert(0, c);
        }

        for (char c : rightChars.toCharArray()) {
            if (!isAllowedBashFunctionCharacter(c)) {
                break;
            }
            sbFunctionName.append(c);
        }

        info.functionName = sbFunctionName.toString();
        info.fileName = sbFileName.toString();
        return info;
    }

    private static boolean isAllowedBashFunctionCharacter(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        if (Character.isJavaIdentifierStart(c)) {
            return c != '$'; // in java allowed, but not for bash...
        }
        return false;
    }

    private static boolean isAllowedFilePathCharacter(char c) {
        if (Character.isLetterOrDigit(c)) {
            return true;
        }
 
        switch(c) {
        case '_':
        case '-':
        case '.':
        case '\\':
        case '/':
            return true;
        }
        return false;
    }

}
