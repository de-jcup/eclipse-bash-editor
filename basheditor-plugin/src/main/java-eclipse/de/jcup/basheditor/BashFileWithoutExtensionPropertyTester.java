package de.jcup.basheditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class BashFileWithoutExtensionPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "de.jcup.basheditor";
	public static final String PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION = "isBashFileWithoutExtension";

	private static final LineIsBashSheBangValidator SHEBANG_VALIDATOR = new LineIsBashSheBangValidator();

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION.equals(property)) {
			return false;
		}
		if (!(receiver instanceof IFile)) {
			/* not supported */
			return false;
		}

		IFile file = (IFile) receiver;
		if (!file.exists()) {
			return false;
		}
		boolean isBashFileWithoutFileExtension = file.getFileExtension() == null;
		if (!isBashFileWithoutFileExtension) {
			return false;
		}
		File theFile;
		try {
			theFile = BashEditorUtil.toFile(file);
		} catch (CoreException e) {
			BashEditorUtil.logError("Was not able to test if file is a bash file:" + file.getName(), e);
			return false;
		}
		if (theFile == null || !theFile.exists()) {
			return false;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(theFile)))) {
			String line = br.readLine();
			if (SHEBANG_VALIDATOR.isValid(line)) {
				return true;
			}
			return false;

		} catch (IOException e) {
			BashEditorUtil.logError("Was not able to test if file is a bash file:" + file.getName(), e);
			return false;
		}

	}

}