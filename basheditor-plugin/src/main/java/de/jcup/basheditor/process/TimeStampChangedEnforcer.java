package de.jcup.basheditor.process;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampChangedEnforcer {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("ss");

	public void ensureNextWriteChangesFileStamp(File file) {
		if (file == null) {
			throw new IllegalArgumentException("No file given but null!");
		}

		long lastModified = file.lastModified();
		/* as long as we got same second we must wait for next one*/

		int modifiedSecond = getSecond(new Date(lastModified));
		while (modifiedSecond==getSecond(new Date())) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
	}
	
	private int getSecond(Date date) {
		String secondsAsString = dateFormat.format(date);
		return Integer.parseInt(secondsAsString);
	}

}
