package de.jcup.basheditor;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceInputStreamProvider {

	public InputStream getStreamFor(String path) throws IOException;
}
