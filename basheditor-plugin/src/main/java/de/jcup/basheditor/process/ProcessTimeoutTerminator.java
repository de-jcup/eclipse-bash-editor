/*
 * Copyright 2016 Albert Tregnaghi
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

public class ProcessTimeoutTerminator {

	/**
	 * Wait for check in milliseconds
	 */
	static final int WAIT_FOR_CHECK = 200;

	private Process process;
	private long timeStarted;
	private OutputHandler outputHandler;
	private long timeOutInSeconds;

	private Thread timeoutCheckThread;

	public ProcessTimeoutTerminator(Process process, OutputHandler outputHandler, long timeOutInSeconds) {
		this.timeOutInSeconds = timeOutInSeconds;
		this.process = process;
		this.outputHandler = outputHandler;
	}

	/**
	 * Does a restart of terminator timeout
	 */
	public void reset() {
		resetTimeStarted();
	}

	private void resetTimeStarted() {
		timeStarted = System.currentTimeMillis();
	}

	/**
	 * Starts time out terminator
	 */
	public void start() {
		if (timeOutInSeconds == ProcessExecutor.ENDLESS_RUNNING) {
			/*
			 * when endless running is active the thread makes no sense, so just do a guard
			 * close
			 */
			return;
		}
		if (isRunning()) {
			reset();
			return;
		}
		timeoutCheckThread = new Thread(new TimeOutTerminatorRunnable(), "process-timeout-terminator");
		timeoutCheckThread.start();
	}

	public boolean isRunning() {
		return timeoutCheckThread != null && timeoutCheckThread.isAlive();
	}

	private class TimeOutTerminatorRunnable implements Runnable {

		@Override
		public void run() {

			long timeOutInMillis = timeOutInSeconds * 1000;

			resetTimeStarted();

			while (process.isAlive()) {
				try {
					Thread.sleep(WAIT_FOR_CHECK);
				} catch (InterruptedException e) {
					break;
				}
				long timeAlive = System.currentTimeMillis() - timeStarted;
				if (timeAlive > timeOutInMillis) {
					if (!process.isAlive()) {
						/*
						 * no termination necessary, process already terminated
						 */
						break;
					}
					outputHandler.output("Timeout reached (" + timeOutInSeconds + " seconds) - destroy process");
					process.destroy();
					break;
				}
			}
		}
	}

}