package de.jcup.basheditor;

public class SimpleAssert {

	public static void notNull(Object obj, String message) {
		if (obj!=null) {
			return;
		}
		throw new IllegalStateException(message);
	}
}
