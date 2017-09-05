package de.jcup.basheditor;

public class SimpleStringUtils {
	
	public static boolean equals(String text1, String text2) {
		if (text1 == null) {
			if (text2 == null) {
				return true;
			}
			return false;
		}
		if (text2 == null) {
			return false;
		}
		return text2.equals(text1);
	}
}
