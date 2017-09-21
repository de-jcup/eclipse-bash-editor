/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this editorFile except in compliance with the License.
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
package de.jcup.basheditor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.basheditor.script.BashFunction;

/**
 * Hyperlink detector for all kind of hyperlinks in egradle editor.
 * 
 * @author Albert Tregnaghi
 *
 */
public class BashHyperlinkDetector extends AbstractHyperlinkDetector {

	private IAdaptable adaptable;

	BashHyperlinkDetector(IAdaptable editor) {
		this.adaptable = editor;
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (adaptable == null) {
			return null;
		}
		BashEditor editor = adaptable.getAdapter(BashEditor.class);
		if (editor == null) {
			return null;
		}
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		int offsetInLine = offset - lineInfo.getOffset();
		String leftChars = line.substring(0, offsetInLine);
		String rightChars = line.substring(offsetInLine);
		StringBuilder sb = new StringBuilder();
		int offsetLeft=offset;
		char[] left = leftChars.toCharArray();
		for (int i=left.length-1; i>=0;i--) {
			char c = left[i];
			if (Character.isWhitespace(c)) {
				break;
			}
			offsetLeft--;
			sb.insert(0,c);
		}
		for (char c : rightChars.toCharArray()) {
			if (Character.isWhitespace(c)) {
				break;
			}
			sb.append(c);
		}
		String functionName = sb.toString();
		BashFunction function = editor.findBashFunction(functionName);
		if (function == null) {
			return null;
		}
		Region targetRegion = new Region(offsetLeft, functionName.length());
		return new IHyperlink[] { new BashFunctionHyperlink(targetRegion, function, editor) };
	}

}
