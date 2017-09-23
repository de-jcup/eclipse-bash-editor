/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.basheditor.document.keywords;

public enum BashSystemKeyWords implements DocumentKeyWord {
	
	SHA_BANG("#!/bin/bash"), 
	
	/* TODO ATR, 24.09.2017: the outcomment part does not work complete, because it is possible to doe something like
	 * "a|b" and not only "a | b" so the simple keyword detection mechansim would not work
	 * I let the outcommented part - to show possibilities only. Maybe a dedicated redirect rule would make 
	 * sense here. But if so also an addtional colour should be introduced for those operation parts to differ from normal
	 * keywords.
	 */
//	DO_IN_BACKGROUND("&"),
//	// see http://tldp.org/HOWTO/Bash-Prog-Intro-HOWTO.html
//	AND_PREVIOUS_COMMAND_COMPLETED_SUCESSFUL("&&"),
//	
//	PIPE("|"),
//	
//	// see http://tldp.org/HOWTO/Bash-Prog-Intro-HOWTO-3.html ("all about redirection")
//	
//	REDIRECT__STDOUT_2_FILE(">"),
//	
//	REDIRECT__STDERR_2_FILE("2>"),
//	
//	REDIRECT__STDOUT_2_STDERR("1>&2"),
//	
//	REDIRECT__STDERR_2_STDOUT("2>&1"),
//	
//	REDIRECT_STDERR_AND_STDOUT_2_FILE("&>"),
//	
//	
//	
//	REDIRECT_AND_APPEND__STDOUT_2_FILE(">>"),
//	
//	REDIRECT_AND_APPEND__STDERR_2_FILE("2>>"),
//	
//	REDIRECT_AND_APPEND__STDOUT_2_STDERR("1>>&2"),
//	
//	REDIRECT_AND_APPEND__STDERR_2_STDOUT("2>>&1"),
//	
//	REDIRECT_AND_APPEND__STDERR_AND_STDOUT_2_FILE("&>>"),
	
	;

	private String text;

	private BashSystemKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}


	@Override
	public boolean isBreakingOnEof() {
		return false;
	}
}
