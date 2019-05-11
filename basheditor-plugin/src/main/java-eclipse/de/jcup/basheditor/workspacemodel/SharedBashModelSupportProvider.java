package de.jcup.basheditor.workspacemodel;

import org.eclipse.core.resources.IFile;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.LineIsBashSheBangValidator;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.workspacemodel.AbstractConfigurableModelBuilderSupportProvider;
import de.jcup.eclipse.commons.workspacemodel.ModelBuilder;

public class SharedBashModelSupportProvider extends AbstractConfigurableModelBuilderSupportProvider<SharedBashModel> {

    private LineIsBashSheBangValidator sheBangValidator;

    public SharedBashModelSupportProvider(PluginContextProvider provider) {
        super(provider);
        sheBangValidator = new LineIsBashSheBangValidator();
    }

    @Override
    public boolean isFileHandled(IFile file) {
        String fileExtension = file.getFileExtension();
        if (fileExtension == null) {
            return false;
        }
        if (fileExtension.isEmpty() || fileExtension.contentEquals("sh") || fileExtension.contentEquals("bash")) {
            return true;
        }
        return false;
    }

    @Override
    public String getModelName() {
        return "Shared bash model";
    }

    @Override
    public boolean isLineCheckforModelNessary(String line, int lineNumber, String[] lines) {
        if (lineNumber > 1) {
            return false;
        }
        /* only when the file is a valid bash script we add it to shared model */
        return sheBangValidator.isValid(line);
    }

    @Override
    public ModelBuilder<SharedBashModel> createBuilder() {
        return new SharedBashModelBuilder(getPluginContextProvider());
    }

    @Override
    public boolean isModelBuilderSupportEnabled() {
        return BashEditorPreferences.getInstance().isSharedModelBuildEnabled();
    }

}
