/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an"AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.basheditor.document.keywords;

// see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
public enum BashLanguageKeyWords implements DocumentKeyWord {
	/* @formatter:off*/
	/* ---------------- */
	/* Reserved words : https://www.gnu.org/software/bash/manual/html_node/Reserved-Word-Index.html
	/* ----------------*/
	
	/* C */
	CASE("case"),

	/* D */ 
	DO("do"),
	DONE("done"),

	/* E */
	ELIF("elif"),
	ELSE("else"),
	ESAC("esac"),
	
	/* F */
	FI("fi"),
	FOR("for"),
	FUNCTION("function"),	

	/* I*/
	IF("if"),
	IN("in"),

	/* S */
	SELECT("select"),
	
	/* T */
	THEN("then"),
	TIME("time"),

	/* U */
	UNTIL("until"),
	
	/* W */
	WHILE("while"),

	

	/* Built in commands */
	// see https://askubuntu.com/questions/512918/how-do-i-list-all-available-shell-builtin-commands
	ALIAS                 ("alias"),
	BG                    ("bg"),
	BIND                  ("bind"),
	BREAK                 ("break"),
	BUILTIN               ("builtin"),
	CALLER                ("caller"),
	CD                    ("cd"),
	COMMAND               ("command"),
	COMPGEN               ("compgen"),
	COMPLETE              ("complete"),
	COMPOPT               ("compopt"),
	CONTINUE              ("continue"),
	DECLARE               ("declare"),
	DIRS                  ("dirs"),
	DISOWN                ("disown"),
	ECHO                  ("echo"),
	ENABLE                ("enable"),
	EVAL                  ("eval"),
	EXEC                  ("exec"),
	EXIT                  ("exit"),
	EXPORT                ("export"),
	FALSE                 ("false"),
	FC                    ("fc"),
	FG                    ("fg"),
	GETOPTS               ("getopts"),
	HASH                  ("hash"),
	HELP                  ("help"),
	HISTORY               ("history"),
	JOBS                  ("jobs"),
	KILL                  ("kill"),
	LET                   ("let"),
	LOCAL                 ("local"),
	LOGOUT                ("logout"),
	MAPFILE               ("mapfile"),
	POPD                  ("popd"),
	PRINTF                ("printf"),
	PUSHD                 ("pushd"),
	PWD                   ("pwd"),
	READ                  ("read"),
	READARRAY             ("readarray"),
	READONLY              ("readonly"),
	RETURN                ("return"),
	SET                   ("set"),
	SHIFT                 ("shift"),
	SHOPT                 ("shopt"),
	SOURCE                ("source"),
	SUSPEND               ("suspend"),
	TEST                  ("test"),
	TIMES                 ("times"),
	TRAP                  ("trap"),
	TRUE                  ("true"),
	TYPE                  ("type"),
	TYPESET               ("typeset"),
	ULIMIT                ("ulimit"),
	UMASK                 ("umask"),
	UNALIAS               ("unalias"),
	UNSET                 ("unset"),
	WAIT                  ("wait"),
	;
	/* @formatter:on*/

	private String text;
	private boolean breaksOnEof;

	private BashLanguageKeyWords(String text) {
		this(text,false);
	}

	private BashLanguageKeyWords(String text, boolean breaksOnEof) {
		this.text = text;
		this.breaksOnEof=breaksOnEof;
	}

	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public boolean isBreakingOnEof() {
		return breaksOnEof;
	}
}
