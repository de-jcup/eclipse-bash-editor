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
import de.jcup.basheditor.debug.DebugBashCodeBuilder;
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

	public BashNetworkConnector(ServerSocket serverSocket, DebugBashCodeBuilder builder) throws IOException {
		this.serverSocket = serverSocket;
		this.builder = builder;
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
	
	public void connect() throws IOException {
		socket = serverSocket.accept();
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();

	}

	public void cancel() throws IOException {
		serverSocket.close();
	}

	public void disconnect() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
	}

	public void stepBegin() throws IOException {
		String command = builder.buildRemoteDebugCommand();
		outputStream.write(command.getBytes());
		outputStream.flush();

		byte[] buffer = new byte[100 * 1024];
		int numberOfLines = 0;
		for (int i = 0; i < buffer.length; i++) {
			int n = inputStream.read();
			if (n == '\n') {
				numberOfLines++;
			} else {
				if (n == '\t' && numberOfLines > 10) {
					String stepStr = new String(buffer, 0, i);
					parse(stepStr);
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

	private void parse(String stepSourceCode) {
		String[] sourceCodeLines = stepSourceCode.split("\n");
		bashVariables.clear();

		for (String sourceCodeLine : sourceCodeLines) {
			addBashVariables(sourceCodeLine);
		}
		Collections.sort(bashVariables);
	}


	private void addBashVariables(String sourceCodeLine) {
		try {
			List<ParseToken> parsed = parser.parse(sourceCodeLine);
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
							identifierToken= it.next();
							if (it.hasNext()) {
								contentToken = it.next();
								variable.addArrayValue(builder.buildSafeArrayValue(contentToken.getText()));
							}
							
						}
					} else {
						variable.setValue(value);
					}

					bashVariables.add(variable);
				}
			}
		} catch (TokenParserException e) {
			EclipseUtil.logError("Parse problems at line:" + sourceCodeLine, e, BashEditorActivator.getDefault());
		}
	}

	public void stepEnd() throws IOException {
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

}
