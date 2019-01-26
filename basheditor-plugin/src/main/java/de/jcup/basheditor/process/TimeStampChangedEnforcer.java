/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
