package de.jcup.basheditor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.jcup.basheditor.ResourceInputStreamProvider;

public class EclipseResourceInputStreamProvider implements ResourceInputStreamProvider{

	@Override
	public InputStream getStreamFor(String path) throws IOException{
		URL url = null;
		try{
			url = new URL("platform:/plugin/de.jcup.basheditor"+path);
		}catch(MalformedURLException e){
			return null;
		}
		return url.openConnection().getInputStream();
	}
}
