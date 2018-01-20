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

public enum BashSpecialVariableKeyWords implements DocumentKeyWord {

	// http://tldp.org/LDP/abs/html/internalvariables.html
	BASH,

	BASH_ENV,

	BASH_SUBSHELL, 
	
	BASHPID, 
	
	BASH_VERSINFO, 
	
	BASH_VERSION,
	
	CDPATH, 
	
	DIRSTACK, 
	
	EDITOR, 
	
	EUID, 
	
	FUNCNAME, 
	
	GLOBIGNORE, 
	
	GROUPS, 
	
	HOME, 
	
	HOSTNAME, 
	
	HOSTTYPE, 
	
	IFS, IGNOREEOF, LC_COLLATE, LC_CTYPE, LINENO, MACHTYPE, 
	
	OLDPWD, OSTYPE, PATH, PIPESTATUS, PPID, PROMPT_COMMAND, 
	
	PS1, PS2, PS3, PS4, PWD("PWD-variable"), 
	
	REPLY, SECONDS, SHELLOPTS, SHLVL, TMOUT, UID,;

	private String text;

	private BashSpecialVariableKeyWords() {
		this(null);
	}

	private BashSpecialVariableKeyWords(String tooltipId) {
		this.text = "$" + name();
		if (tooltipId == null) {
			tooltipId = name();
		}
		tooltip = TooltipTextSupport.getTooltipText(tooltipId);
		if (tooltip == null || tooltip.isEmpty()) {
			tooltip = "An internal bash variable. See online documentation for mor information.";
		}
		this.linkToDocumentation = "http://tldp.org/LDP/abs/html/internalvariables.html";
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isBreakingOnEof() {
		return false;
	}

	private String tooltip;
	private String linkToDocumentation;

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public String getLinkToDocumentation() {
		return linkToDocumentation;
	}
}
