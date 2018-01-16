package de.jcup.basheditor.document.keywords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class TooltipTextSupport {
	private static final TooltipTextSupport INSTANCE = new TooltipTextSupport();
	
	public static String getTooltipText(String id){
		return INSTANCE.get(id);
	}
	private Map<String, String> idToTooltipCache = new TreeMap<>();

	TooltipTextSupport(){
		
	}
	
	/**
	 * Resolves tool tip for given id from text files inside tool tip-folder. String result will be cached
	 * @param id
	 * @return tool tip
	 */
	public String get(String id) {
		if (id == null) {
			return "";
		}
		String tooltip = idToTooltipCache.get(id);

		if (tooltip == null) {
			tooltip = load(id);
			idToTooltipCache.put(id, tooltip);
		}
		return tooltip;
	}

	/**
	 * Load tool tip
	 * 
	 * @param id
	 * @return tool tip string - never <code>null</code>
	 */
	String load(String id) {
		if (id==null){
			throw new IllegalArgumentException("id may not be null");
		}
		String path = "/tooltips/" + id + ".txt";
		try (InputStream stream = TooltipTextSupport.class.getResourceAsStream(path)) {
			if (stream == null) {
				return "";
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();

		} catch (IOException e) {
			/* should not happen - but if there are errors
			 * we just return an empty string
			 */
			return "";
		}

	}
}
