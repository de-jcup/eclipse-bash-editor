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
package de.jcup.basheditor.debug;

public interface BashDebugInfoProvider {

    /**
     * Gets hard real user home as file - normally "user.home" system property of java
     * @return
     */
    String getSystemUserHomePath();
    
    /**
     * @return get default path to user home. On windows system this will changed to MinGW style per default
     */
    String getDefaultScriptPathToUserHome();
    
    /**
     * @return the path to user home relevant for script. When a custom script path to user home is defined this will be used, otherwise the {@link #getDefaultScriptPathToUserHome()}
     * variantFullText
     */
    String getResultingScriptPathToUserHome();

}
