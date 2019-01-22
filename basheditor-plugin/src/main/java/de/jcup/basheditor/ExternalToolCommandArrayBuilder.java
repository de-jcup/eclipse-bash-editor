package de.jcup.basheditor;

import java.io.File;

public class ExternalToolCommandArrayBuilder {
	
	private int numKeywordsReplaced = 0;

	public String[] build(String externalToolCall, File editorFile) {
		numKeywordsReplaced = 0;
		String[] ret = externalToolCall.split(" ");
		
		// detect special placeholder(s):
		for (int i=0; i < ret.length; i++)
			if (ret[i].equalsIgnoreCase("$filename"))
			{
				ret[i] = editorFile.toPath().toString();
				numKeywordsReplaced++;
			}
		
		return ret;
	}

	public int getNumKeywordsReplaced() {
		return numKeywordsReplaced;
	}
}
