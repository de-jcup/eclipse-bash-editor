package de.jcup.basheditor.workspacemodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.basheditor.script.BashScriptModelBuilder;
import de.jcup.basheditor.script.BashScriptModelException;
import de.jcup.eclipse.commons.EclipseResourceHelper;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.workspacemodel.AbstractModelBuilder;
import de.jcup.eclipse.commons.workspacemodel.ModelUpdateAction;

/**
 * This is an example for a project model builder. This builder collects in
 * every file with ".testcase" file extensions the lines where wellknown
 * keywords reside.<br>
 * <br>
 * Inside it's just a simple map .
 * 
 * @author albert
 *
 */
public class SharedBashModelBuilder extends AbstractModelBuilder<SharedBashModel> {

    BashScriptModelBuilder scriptModelBuilder;
    private PluginContextProvider provider;

    SharedBashModelBuilder(PluginContextProvider provider) {
        scriptModelBuilder = new BashScriptModelBuilder();

        this.provider = provider;
    }

    @Override
    public SharedBashModel create() {
        return new SharedBashModel();
    }

    @Override
    public void updateImpl(SharedBashModel model, ModelUpdateAction action) {
        IResource resource = action.getResource();
        if (! (resource instanceof IFile)) {
            return;
        }
        IFile file = (IFile) resource;
        switch (action.getType()) {
        case ADD:
            String loadedscript = loadScript(file);
            System.out.println("added script:"+file.getName());
            try {
                /*
                 * the builder support already has checked that this file is a bash file - means
                 * a bash shebang was found. So normally ... .there should be no exceptions.
                 */
                BashScriptModel scriptModel = scriptModelBuilder.build(loadedscript);
                model.update(file, scriptModel);
            } catch (BashScriptModelException e) {
                EclipseUtil.logError("Was not able build script", e, provider);
            }
            break;
        case DELETE:
            model.remove(file);
            System.out.println("removed script:"+file.getName());
            break;
        default:
            break;

        }

    }

    private String loadScript(IFile file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        try {
            return EclipseResourceHelper.DEFAULT.readAsText(file, provider, name);
        } catch (CoreException e) {
            EclipseUtil.logError("Was not able to load script:" + name, e, provider);
            return null;
        }
    }

}
