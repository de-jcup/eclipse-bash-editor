package de.jcup.basheditor;

public class LineIsBashSheBangValidator {

	public boolean isValid(String line) {
		if (line == null) {
			return false;
		}
		if (line.isEmpty()) {
			return false;
		}
		if (!line.startsWith("#!")) {
			return false;
		}
		if (line.indexOf("bash") == -1) {
			return false;
		}
		return true;
	}
}
