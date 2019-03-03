package de.jcup.basheditor;

import java.net.ServerSocket;

import de.jcup.basheditor.debug.BashDebugConstants;

public class TestTool {

	public static void main(String[] args) throws Exception{
		
		simulateZombiDebugSession(BashDebugConstants.DEFAULT_DEBUG_PORT);
	}

	
	/**
	 * Simulates a zombi debug session on given port 
	 * Will just block the socket...
	 * @param port
	 * @throws Exception
	 */
	public static void simulateZombiDebugSession(int port) throws Exception {
		System.out.println("Simulate zombi debug session on port:"+port);
		/* @formatter: off*/
		TestTool testTool = new TestTool();
		testTool.
			bindSocket(port).
			sleep(600).
			unbindSocket(port);
		/* @formatter: on*/
	}

	private ServerSocket serverSocket;

	public TestTool bindSocket(int port) throws Exception{
		serverSocket = new ServerSocket(port); 
		serverSocket.accept();
		return this;
	}
	
	public TestTool unbindSocket(int port) throws Exception{
		if (serverSocket!=null) {
			serverSocket.close();
		}
		return this;
	}
	
	public TestTool sleep(int seconds) throws Exception{
		Thread.sleep(seconds*1000); 
		return this;
	}
}
