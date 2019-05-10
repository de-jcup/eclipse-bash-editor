package de.jcup.basheditor;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ResourceUtil {

    public static void openInEditor(File file) throws PartInitException{
        IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(file.getAbsolutePath()));

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IDE.openEditorOnFileStore(page, fileStore);
    }
}
