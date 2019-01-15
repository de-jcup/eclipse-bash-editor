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

import java.io.IOException;

public interface ProcessExecutor {

	public static final Integer PROCESS_RESULT_OK = Integer.valueOf(0);

	public static final int ENDLESS_RUNNING = 0;

	/**
	 * Execute commands in given working directory. Is done in same thread. Will
	 * wait until execution result is available (process terminates...)
	 * 
	 * @param wdProvider
	 * @param envProvider
	 * @param processContext context for process operations and states
	 * @param commands
	 * @return result code
	 * @throws IOException
	 */
	public int execute(ProcessConfiguration wdProvider, EnvironmentProvider envProvider, ProcessContext processContext,
			String... commands) throws IOException;
}