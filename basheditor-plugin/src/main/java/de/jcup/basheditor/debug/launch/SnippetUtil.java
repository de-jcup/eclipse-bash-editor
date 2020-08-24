package de.jcup.basheditor.debug.launch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SnippetUtil {

	public interface SnippetFactory {
		public String createSnippet();
	}

	public static File ensureExecutableFile(File killFile, SnippetFactory factory) throws IOException {
		/* ensure debug script file does really exist on user.home */
		if (killFile.exists()) {
			return killFile;
		}
		killFile.getParentFile().mkdirs();
		killFile.createNewFile();
		killFile.setExecutable(true, true);

		String snippet = factory.createSnippet();
		try (FileWriter fw = new FileWriter(killFile); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(snippet);
		}
		return killFile;
	}
}
