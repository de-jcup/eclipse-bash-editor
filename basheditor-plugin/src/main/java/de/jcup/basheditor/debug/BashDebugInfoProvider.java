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
     * variant
     */
    String getResultingScriptPathToUserHome();

}
