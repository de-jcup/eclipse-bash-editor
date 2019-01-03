package de.jcup.basheditor.process;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class BashEditorFileProcessContext implements ProcessContext, ProcessConfiguration, EnvironmentProvider {

	private Map<String, String> envMap;
	private File editorFile;
	private CancelStateProvider cancelStateProvider;

	public BashEditorFileProcessContext(File editorFile) {
		envMap = new TreeMap<>();
		this.editorFile = editorFile;
	}

	@Override
	public Map<String, String> getEnvironment() {
		return envMap;
	}

	@Override
	public String getWorkingDirectory() {
		return editorFile.getParent();
	}

	@Override
	public CancelStateProvider getCancelStateProvider() {
		if (cancelStateProvider == null) {
			return CancelStateProvider.NEVER_CANCELED;
		}
		return cancelStateProvider;
	}

	public void setCancelStateProvider(CancelStateProvider cancelStateProvider) {
		this.cancelStateProvider = cancelStateProvider;
	}

}
