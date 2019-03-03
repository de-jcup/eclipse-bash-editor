/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.basheditor.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.EclipseDeveloperSettings;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.script.parser.ParseToken;
import de.jcup.basheditor.script.parser.TokenParser;
import de.jcup.basheditor.script.parser.TokenParserException;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashNetworkConnector {
	private ServerSocket serverSocket;
	private Socket socket;

	private InputStream inputStream;
	private OutputStream outputStream;

	private BashNetworkVariableData bashLineNumber;
	private BashNetworkVariableData functionName;
	private BashNetworkVariableData bashSource;

	private Vector<BashNetworkVariableData> bashVariables = new Vector<BashNetworkVariableData>();
	private TokenParser parser = new TokenParser();
	private DebugBashCodeBuilder builder;
	private int port;

	public BashNetworkConnector(int port) {
		this.port = port;
		this.builder = new DebugBashCodeBuilder();
	}

	public BashNetworkVariableData getBashLineNumber() {
		return bashLineNumber;
	}

	public BashNetworkVariableData getBashSource() {
		return bashSource;
	}

	public BashNetworkVariableData getFunctionName() {
		return functionName;
	}

	public void startServerSocket() throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public void connect() throws IOException {
		socket = serverSocket.accept();

		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
	}

	public void cancel() throws IOException {
		disconnect();
	}

	public void disconnect() throws IOException {
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	public void stepBegin() throws IOException {
		if (isShowingMetaInformation()) {
			BashDebugConsole.println(">> =============== <<");
			BashDebugConsole.println(">> Begin new step  <<");
			BashDebugConsole.println(">> =============== <<");
		}
		bashVariables.clear();

		String command = builder.buildRemoteDebugCommand();
		outputStream.write(command.getBytes());
		outputStream.flush();

		StepParseContext spc = new StepParseContext();

		byte[] buffer = new byte[100 * 1024];
		int numberOfLines = 0;
		for (int i = 0; i < buffer.length; i++) {
			int n = inputStream.read();
			if (n == '\n') {
				numberOfLines++;
			} else {
				if (n == '\t' && numberOfLines > builder.getLinesOfDebugCode()) {
					spc.stepStr = new String(buffer, 0, i);
					parse(spc);
					break;
				} else {
					numberOfLines = 0;
				}
			}
			buffer[i] = (byte) n;
		}

		int indexToRemove = -1;
		for (int i = 0; i < functionName.getArraySize(); i++) {
			if (isTrapFunction(i)) {
				indexToRemove = i;
				break;
			}

		}
		if (indexToRemove != -1) {
			functionName.removeFromArray(indexToRemove);
			bashSource.removeFromArray(indexToRemove);
		}
	}

	private boolean isTrapFunction(int functionIndex) {
		String nameOfFirstFunction = functionName.getStringValue(functionIndex);
		return nameOfFirstFunction.equalsIgnoreCase(builder.getNameOfTrapFunction());
	}

	private void parse(StepParseContext spc) {
		String stepSourceCode = spc.stepStr;
		String[] sourceCodeLines = stepSourceCode.split("\n");

		for (String sourceCodeLine : sourceCodeLines) {
		    if (isShowingMetaInformation()) {
		        BashDebugConsole.println("Parse:" + sourceCodeLine);
		    }
			addBashVariables(spc, sourceCodeLine);
		}
		Collections.sort(bashVariables);
	}

	private class StepParseContext {
		private String stepStr;
		private boolean trapFunctionFound;

		public boolean isTrapFunctionAlreadyFound() {
			return trapFunctionFound;
		}
	}

	private void addBashVariables(StepParseContext context, String sourceCodeLine) {
		if (context.isTrapFunctionAlreadyFound()) {
			/*
			 * trap function is first function which will be found. After this output is
			 * only function content. We must ignore next lines for variables, because
			 * current variable content is already known and parsing more will result in
			 * duplicates (code scan in functions...)
			 */
			return;
		}
		if (sourceCodeLine.startsWith(builder.getNameOfTrapFunction())) {
			if (isShowingMetaInformation()) {
				BashDebugConsole.println(">>>Function found, mark end of variables reached:" + sourceCodeLine);
			}
			context.trapFunctionFound = true;
			return;
		}
		try {
			List<ParseToken> parsed = parser.parse(sourceCodeLine);
			if (isShowingMetaInformationInTraceMode()) {
				BashDebugConsole.println(">>>Tokens found:"+parsed);
			}
			// e.g. [EXPRESSION:'BASH_ARGC=', EXPRESSION:'([0]=', STRING:'"6")']
			// e.g. [EXPRESSION:'BASH_ARGV=', EXPRESSION:'([0]=', STRING:'"-d"',
			// EXPRESSION:'[1]=', STRING:'"1.0.0"', EXPRESSION:'[2]=', STRING:'"-s"',
			// EXPRESSION:'[3]=', STRING:'"int"', EXPRESSION:'[4]=', STRING:'"-e"',
			// EXPRESSION:'[5]=', STRING:'"mycmd")']
			BashNetworkVariableData variable = null;
			for (Iterator<ParseToken> it = parsed.iterator(); it.hasNext();) {
				ParseToken token = it.next();

				if (token.isVariableDefinition()) {
					String variableName = token.getTextAsVariableName();
					variable = new BashNetworkVariableData(variableName);
					if (variableName.equals("_") || variableName.isEmpty()) {
						return;
					} else if (variableName.equals(builder.getNameOfDebugCommand())) {
						return;
					} else if (variableName.equals(builder.getNameOfBashLineNumberVariable())) {
						bashLineNumber = variable;
					} else if (variableName.equals(builder.getNameOfFunctionNameVariable())) {
						functionName = variable;
					} else if (variableName.equals(builder.getNameOfBashSourceVariable())) {
						bashSource = variable;
					}
					if (!it.hasNext()) {
						return;
					}
					ParseToken identifierToken = it.next();
					String value = identifierToken.getText();
					if (value.startsWith("(")) {
						variable.defineAsArray();
						variable.setValue(sourceCodeLine);
						if (!it.hasNext()) {
							break;
						}
						ParseToken contentToken = it.next();
						variable.addArrayValue(builder.buildSafeArrayValue(contentToken.getText()));

						while (it.hasNext()) {
							identifierToken = it.next();
							if (it.hasNext()) {
								contentToken = it.next();
								variable.addArrayValue(builder.buildSafeArrayValue(contentToken.getText()));
							}

						}
					} else {
						variable.setValue(value);
					}
					if (isShowingMetaInformationInTraceMode()) {
						BashDebugConsole.println(">> found + add variable:"+variable);
					}
					bashVariables.add(variable);
				}
			}
		} catch (TokenParserException e) {
			EclipseUtil.logError("Parse problems at line:" + sourceCodeLine, e, BashEditorActivator.getDefault());
		}
	}

    private boolean isShowingMetaInformationInTraceMode() {
        return EclipseDeveloperSettings.SHOW_METAINFORMATION_TRACEMODE && isShowingMetaInformation();
    }

    private boolean isShowingMetaInformation() {
        return BashEditorPreferences.getInstance().isShowMetaInfoInDebugConsoleEnabled();
    }

	public void stepEnd() throws IOException {
		if (!isConnected()) {
			return;
		}
		outputStream.write("\n".getBytes());
		outputStream.flush();
	}

	public void terminate() throws IOException {
		String command = "exit 0\n";
		outputStream.write(command.getBytes());
		outputStream.flush();
		serverSocket.close();
	}

	public int getVariableCount() {
		return bashVariables.size();
	}

	public BashNetworkVariableData getVariableData(int i) {
		return bashVariables.get(i);
	}

	public boolean isConnected() {
		if (socket == null) {
			return false;
		}
		return socket.isConnected();
	}

}
