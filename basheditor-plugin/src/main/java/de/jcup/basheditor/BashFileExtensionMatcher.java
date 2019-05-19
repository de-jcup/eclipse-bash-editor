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
package de.jcup.basheditor;

public class BashFileExtensionMatcher {
    public boolean isMatching(String fileExtension) {
        return isMatching(fileExtension,true);
    }
    public boolean isMatching(String fileExtension, boolean removePoint) {
        if (fileExtension==null) {
            return true;
        }
        if (fileExtension.isEmpty()) {
            return true;
        }
        String safeFileExtension=fileExtension;
        if (safeFileExtension.startsWith(".")) {
            if (!removePoint) {
                return false;
            }
            if (safeFileExtension.length()==1) {
                // "xyz." resp. "." is not supported
                return false;
            }
            safeFileExtension=fileExtension.substring(1);
        }
        if (safeFileExtension.contentEquals("bash")) {
            return true;
        }
        if (safeFileExtension.contentEquals("sh")) {
            return true;
        }
        return false;
    }

}
