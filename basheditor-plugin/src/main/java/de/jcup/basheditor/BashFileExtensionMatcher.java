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
