package de.jcup.basheditor.process;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TimeStampChangedEnforcerTest {

	private TimeStampChangedEnforcer enforcerToTest;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("ss");

	@Before
	public void before() {
		enforcerToTest = new TimeStampChangedEnforcer();
	}
	
	@Test
	public void ensureNextWriteChangesFileStamp_waits_for_next_second_when_modification_time_stamp_set() throws Exception{
		/* prepare */
		File file = File.createTempFile("timecheck", "txt");
		Date modificationDate = new Date();
		file.setLastModified(modificationDate.getTime()); // more than one second in future
		int secondsNow = getSecond(modificationDate);
		
		
		/* execute */
		enforcerToTest.ensureNextWriteChangesFileStamp(file);
		
		/* test */
		Date afterEnsure = new Date();
		int secondsAfterEnsure = getSecond(afterEnsure);

		assertTrue("ensured time must be after timestamp", afterEnsure.getTime()>modificationDate.getTime());
		assertTrue("seconds must differ!",secondsNow!=secondsAfterEnsure);
		
		
	}
	
	@Test
	public void ensureNextWriteChangesFileStamp_waits_for_next_second_when_NO_modification_time_stamp_set_for_new_file() throws Exception{
		/* prepare */
		File file = File.createTempFile("timecheck", "txt");
		Date now = new Date();
		int secondsNow = getSecond(now);
		
		
		/* execute */
		enforcerToTest.ensureNextWriteChangesFileStamp(file);
		
		/* test */
		Date afterEnsure = new Date();
		int secondsAfterEnsure = getSecond(afterEnsure);

		assertTrue("ensured time must be after timestamp", afterEnsure.getTime()>now.getTime());
		assertTrue("seconds must differ!",secondsNow!=secondsAfterEnsure);
		
		
	}

	private int getSecond(Date date) {
		String secondsAsString = dateFormat.format(date);
		return Integer.parseInt(secondsAsString);
	}

}
