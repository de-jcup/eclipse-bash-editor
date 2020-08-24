package de.jcup.basheditor.debug;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class BashPIDSnippetSupportTest {

	private BashPIDSnippetSupport snippetSupportToTest;
	
	@Before
	public void before() {
		snippetSupportToTest=new BashPIDSnippetSupport(null);
		snippetSupportToTest.bashPIDfileSupport=new BashCallPIDStoreSnippetBuilder() {
			@Override
			public String buildPIDFileAbsolutePath(String port) {
				return "absolutePathToPIDFile";
			}
		};
	}
	
	@Test
    public void buildKillPIDSnippet() throws IOException {
		/* @formatter:off */
		String expectedSnippet= 
				"cd ~/.basheditor\n" + 
				"KILL_TEXTFILE=\"./PID_debug-terminal_port_$1.txt\"\n" + 
				"if [ -f \"$KILL_TEXTFILE\" ]; then\n" + 
				"  while IFS='' read -r LINE || [ -n \"${LINE}\" ]; do\n" + 
				"        kill -9 ${LINE}\n" + 
				"  done < $KILL_TEXTFILE;\n" + 
				"  \n" + 
				"  rm \"$KILL_TEXTFILE\"\n" + 
				"else \n" + 
				"  echo \"No file found :$KILL_TEXTFILE inside pwd=$PWD\"\n" + 
				"fi\n" + 
				"";;

        /* execute + test */
        assertEquals(expectedSnippet, snippetSupportToTest.buildKillOldTerminalsSnippet());
        
        /* @formatter:on */
    }
}
