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

import static de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy.PROJECT;
import static de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy.SCRIPT;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.workspacemodel.SharedModelMethodTarget;

/**
 * Hyperlink detector for functions. Depending of selected LinkFunctionStrategy
 * hyper links for script internal, project scope and workspace scope will be
 * created
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
		HoveredTextInfo textInfo = createTextInfo(textViewer, region);
		if (textInfo == null) {
			return null;
		}
		IHyperlink[] result = createHyperlinksForFunctionsInsideEditor(editor, textInfo);
		if (result != null) {
			return result;
		}
		result = createHyperlinksToFunctionsInExternalFiles(textInfo, canShowMultipleHyperlinks);
		if (result != null) {
			return result;
		}
		result = createHyperlinksToFiles(textInfo, canShowMultipleHyperlinks);
		return result;
	}

	private IHyperlink[] createHyperlinksToFiles(HoveredTextInfo textInfo,
			boolean canShowMultipleHyperlinks) {
		String path = textInfo.text;
		if (path == null) {
			return null;
		}
		IFile file = adaptable.getAdapter(IFile.class);
		if (file==null) {
			return null;
		}
		IContainer parent = file.getParent();
		if (parent==null) {
			return null;
		}
		IFile target = parent.getFile(new Path(path));
		if (target!=null && target.exists()) {
			Region targetRegion = new Region(textInfo.offsetLeft, path.length());
			return new IHyperlink[] {new BashExternalFileHyperlink(targetRegion, target)};
		}
		return null;
	}

	private IHyperlink[] createHyperlinksToFunctionsInExternalFiles(HoveredTextInfo functionInfo,
			boolean canShowMultipleHyperlinks) {
		/* not found internal, so try external variantFullText */

		if (!BashEditorPreferences.getInstance().isSharedModelBuildEnabled()) {
			return null;
		}
		BashEditorLinkFunctionStrategy linkFunctionStrategy = BashEditorPreferences.getInstance()
				.getLinkFunctionStrategy();
		if (SCRIPT.equals(linkFunctionStrategy)) {
			return null;
		}
		IProject project = null;// if kept null its will enforce workspace scope as parameter.
		if (PROJECT.equals(linkFunctionStrategy)) {
			project = adaptable.getAdapter(IProject.class);
		}
		List<SharedModelMethodTarget> foundFunctionCandidates = BashEditorActivator.getDefault().getModel()
				.findResourcesHavingMethods(functionInfo.text, project);
		if (foundFunctionCandidates.isEmpty()) {
			return null;
		}

		boolean mustReduceToFirstEntry = !canShowMultipleHyperlinks && foundFunctionCandidates.size() > 1;
		if (mustReduceToFirstEntry) {
			IHyperlink hyper = createExternalHyperlink(functionInfo, foundFunctionCandidates.iterator().next());
			if (hyper == null) {
				return null;
			}
			return new IHyperlink[] { hyper };
		}
		List<IHyperlink> result = new ArrayList<IHyperlink>();
		for (SharedModelMethodTarget target : foundFunctionCandidates) {
			IHyperlink hyper = createExternalHyperlink(functionInfo, target);
			if (hyper != null) {
				result.add(hyper);
			}
		}

		return result.toArray(new IHyperlink[result.size()]);
	}

	private IHyperlink[] createHyperlinksForFunctionsInsideEditor(BashEditor editor, HoveredTextInfo textInfo) {
		BashFunction function = editor.findBashFunction(textInfo.text);
		if (function != null) {
        	/* we found internal link - so we use this, no external search necessary */
        	Region targetRegion = new Region(textInfo.offsetLeft, textInfo.text.length());
        	return new IHyperlink[] { new BashFunctionHyperlink(targetRegion, function, editor) };
        }
		return null;
	}

	private HoveredTextInfo createTextInfo(ITextViewer textViewer, IRegion region) {
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

		return createTextInfo(offset, lineInfo, line);
	}
	
	private HoveredTextInfo createTextInfo(int offset, IRegion lineInfo, String line) {
	    HoveredTextInfo info = new HoveredTextInfo(offset);
	    String text = fetchText(offset, lineInfo, line, info);
		info.text = text;
		return info;
	}

    private String fetchText(int offset, IRegion lineInfo, String line, HoveredTextInfo info) {
        int offsetInLine = offset - lineInfo.getOffset();
		String leftChars = line.substring(0, offsetInLine);
		String rightChars = line.substring(offsetInLine);
		StringBuilder sb = new StringBuilder();
		char[] left = leftChars.toCharArray();
		for (int i = left.length - 1; i >= 0; i--) {
			char c = left[i];
			if (Character.isWhitespace(c)) {
				break;
			}
			info.offsetLeft--;
			sb.insert(0, c);
		}
		for (char c : rightChars.toCharArray()) {
			if (Character.isWhitespace(c)) {
				break;
			}
			sb.append(c);
		}
		String text = sb.toString();
        return text;
    }

	private IHyperlink createExternalHyperlink(HoveredTextInfo info, SharedModelMethodTarget target) {
		BashFunction function = target.getFunction();
		if (function == null) {
			return null;
		}
		IResource resource = target.getResource();
		if (!(resource instanceof IFile)) {
			return null;
		}
		IFile file = (IFile) resource;
		int offsetLeft = info.offsetLeft;
		String functionName = info.text;

		/* we found internal link - so we use this, no external search necessary */
		Region targetRegion = new Region(offsetLeft, functionName.length());
		BashExternalFunctionHyperlink link = new BashExternalFunctionHyperlink(targetRegion, function, file);
		return link;
	}
	
	private class HoveredTextInfo {
	    public HoveredTextInfo(int offset) {
            this.offsetLeft=offset;
        }
        int offsetLeft;
	    String text;
	}
	
}
