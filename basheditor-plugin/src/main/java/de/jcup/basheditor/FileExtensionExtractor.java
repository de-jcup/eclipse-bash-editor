package de.jcup.basheditor;

import java.io.File;
import java.util.Objects;

public class FileExtensionExtractor {

    public String extractFileExtension(File file) {
        Objects.requireNonNull(file);
        
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index==-1) {
            return null;
        }
        if (fileName.length()==index-1) {
            return null;
        }
        return fileName.substring(index);
        
    }
}
