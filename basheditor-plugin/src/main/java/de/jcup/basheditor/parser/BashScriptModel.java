package de.jcup.basheditor.parser;

import java.util.ArrayList;
import java.util.Collection;

public class BashScriptModel {

	Collection<BashFunction> functions = new ArrayList<BashFunction>();

	public Collection<BashFunction> getFunctions() {
		return functions;
	}

}
