package de.jcup.basheditor;

import java.io.File;

public class ExternalToolCommandArrayBuilder {

	public String[] build(String externalToolCall, File editorFile) {
		String[] ret = externalToolCall.split(" ");
		
		// detect special placeholder(s):
		for (int i=0; i < ret.length; i++)
			if (ret[i].equalsIgnoreCase("$filename"))
				ret[i] = editorFile.toPath().toString();
		
		return ret;
	}
}
