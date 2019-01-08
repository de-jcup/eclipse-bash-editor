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
package de.jcup.basheditor.document;

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.ExternalToolCommandArrayBuilder;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.process.BashEditorFileProcessContext;
import de.jcup.basheditor.process.OutputHandler;
import de.jcup.basheditor.process.SimpleProcessExecutor;

/**
 * Document provider for files inside workspace
 * @author albert
 *
 */
public class BashFileDocumentProvider extends FileDocumentProvider {
	
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			/* installation necessary */
			IDocumentPartitioner partitioner = BashPartionerFactory.create();

			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
	/*
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException
	{
		BashEditorPreferences preferences = BashEditorPreferences.getInstance();
		boolean runExternalTool = preferences.getBooleanPreference(P_SAVE_ACTION_ENABLED);
		if (!runExternalTool)
		{
			super.doSaveDocument(monitor, element, document, overwrite);
			return;
		}
		
		BashEditorUtil.logInfo("Starting doSaveDocument");
		
		// create a temporary file
		String tempFileAbsPath;
		try {
			tempFileAbsPath = writeTempFile(document);
		} catch (IOException e1) {
			BashEditorUtil.logError("Failed generating the temporary file for external tool", e1);
			super.doSaveDocument(monitor, element, document, overwrite);
			return;
		}
		File tempFile = new File(tempFileAbsPath);
		
		// we will run the external tool from the directory where the temporary file is located:
		BashEditorFileProcessContext ctx = new BashEditorFileProcessContext(tempFile);

		// substitute in the external tool cmd line the special placeholders:
		ExternalToolCommandArrayBuilder externalTool = new ExternalToolCommandArrayBuilder();
		String[] cmd_args = externalTool.build(preferences.getStringPreference(P_SAVE_ACTION), tempFile);
		
		BashEditorUtil.logInfo("Running external tool with following cmds args: " + String.join(",", cmd_args));

		// now run external tool
		SimpleProcessExecutor executor = new SimpleProcessExecutor(OutputHandler.NO_OUTPUT, false, );
		try {
			if (executor.execute(ctx, ctx, ctx, cmd_args) == 0) {
				
				String external_tool_result = readFile(tempFileAbsPath, StandardCharsets.UTF_8);
				document.set(external_tool_result);
			}
		} catch (IOException e) {
			BashEditorUtil.logError("Running external tool", e);
			// save the document anyway
		}
		
		super.doSaveDocument(monitor, element, document, overwrite);
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	static String writeTempFile(IDocument contents) throws IOException
	{
		File tempFile;
		tempFile = File.createTempFile("eclipse-basheditor", ".tmp");
		
		// put the current contents in the temp file
		FileWriter fw = new FileWriter(tempFile);
		fw.write(contents.get());
		fw.close();
		
		return tempFile.getAbsolutePath();
	}
	*/
}