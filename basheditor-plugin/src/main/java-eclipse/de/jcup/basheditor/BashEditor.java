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
package de.jcup.basheditor;

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.*;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.basheditor.document.BashFileDocumentProvider;
import de.jcup.basheditor.document.BashTextFileDocumentProvider;
import de.jcup.basheditor.outline.BashEditorContentOutlinePage;
import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.script.BashError;
import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.basheditor.script.BashScriptModelBuilder;

public class BashEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "org.basheditor.editors.BashEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	private BashBracketsSupport bracketMatcher = new BashBracketsSupport();
	private SourceViewerDecorationSupport additionalSourceViewerSupport;
	private BashEditorContentOutlinePage outlinePage;
	private BashScriptModelBuilder modelBuilder;

	public BashEditor() {
		setSourceViewerConfiguration(new BashSourceViewerConfiguration(this));
		this.modelBuilder = new BashScriptModelBuilder();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForThisEditor(event)) {
			int severity = getSeverity();

			setTitleImageDependingOnSeverity(severity);
		}
	}

	void setTitleImageDependingOnSeverity(int severity) {
		if (severity == IMarker.SEVERITY_ERROR) {
			setTitleImage(EclipseUtil.getImage("icons/bash-editor-with-error.png", BashEditorActivator.PLUGIN_ID));
		} else {
			setTitleImage(EclipseUtil.getImage("icons/bash-editor.png", BashEditorActivator.PLUGIN_ID));
		}
	}

	private int getSeverity() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return IMarker.SEVERITY_INFO;
		}
		try {
			final IResource resource = ResourceUtil.getResource(editorInput);
			if (resource == null) {
				return IMarker.SEVERITY_INFO;
			}
			int severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			return severity;
		} catch (CoreException e) {
			// Might be a project that is not open
		}
		return IMarker.SEVERITY_INFO;
	}

	private void addErrorMarkers(BashScriptModel model) {
		if (model == null) {
			return;
		}
		IDocument document = getDocument();
		if (document==null){
			return;
		}
		Collection<BashError> errors = model.getErrors();
		for (BashError error : errors) {
			int startPos = error.getStart();
			int line;
			try {
				line = document.getLineOfOffset(startPos);
			} catch (BadLocationException e) {
				EclipseUtil.logError("Cannot get line offset for " + startPos, e);
				line = 0;
			}
			BashEditorUtil.addScriptError(this, line, error);
		}

	}

	public void setErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	public BashBracketsSupport getBracketMatcher() {
		return bracketMatcher;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		activateBashEditorContext();

		installAdditionalSourceViewerSupport();

		StyledText styledText = getSourceViewer().getTextWidget();
		styledText.addKeyListener(new BashBracketInsertionCompleter(this));

		/*
		 * register as resource change listener to provide marker change
		 * listening
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		setTitleImageInitial();
	}

	public BashEditorContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new BashEditorContentOutlinePage(this);
		}
		return outlinePage;
	}

	/**
	 * Installs an additional source viewer support which uses editor
	 * preferences instead of standard text preferences. If standard source
	 * viewer support would be set with editor preferences all standard
	 * preferences would be lost or had to be reimplmented. To avoid this
	 * another source viewer support is installed...
	 */
	private void installAdditionalSourceViewerSupport() {

		additionalSourceViewerSupport = new SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(),
				getAnnotationAccess(), getSharedColors());
		additionalSourceViewerSupport.setCharacterPairMatcher(bracketMatcher);
		additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(
				P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
				P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), P_EDITOR_ENCLOSING_BRACKETS.getId());

		IPreferenceStore preferenceStoreForDecorationSupport = BashEditorUtil.getPreferences().getPreferenceStore();
		additionalSourceViewerSupport.install(preferenceStoreForDecorationSupport);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (additionalSourceViewerSupport != null) {
			additionalSourceViewerSupport.dispose();
		}
		if (bracketMatcher != null) {
			bracketMatcher.dispose();
			bracketMatcher = null;
		}

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public String getBackGroundColorAsWeb() {
		ensureColorsFetched();
		return bgColor;
	}

	public String getForeGroundColorAsWeb() {
		ensureColorsFetched();
		return fgColor;
	}

	private void ensureColorsFetched() {
		if (bgColor == null || fgColor == null) {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			StyledText textWidget = sourceViewer.getTextWidget();
			if (textWidget == null) {
				return;
			}

			/*
			 * TODO ATR, 03.02.2017: there should be an easier approach to get
			 * editors back and foreground, without syncexec
			 */
			EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
					fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
				}
			});
		}

	}

	private String bgColor;
	private String fgColor;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (BashEditor.class.equals(adapter)) {
			return (T) this;
		}
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) getOutlinePage();
		}
		if (ColorManager.class.equals(adapter)) {
			return (T) getColorManager();
		}
		if (IFile.class.equals(adapter)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feditorInput = (IFileEditorInput) input;
				return (T) feditorInput.getFile();
			}
			return null;
		}
		if (ISourceViewer.class.equals(adapter)) {
			return (T) getSourceViewer();
		}
		if (StatusMessageSupport.class.equals(adapter)) {
			return (T) this;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Jumps to the matching bracket.
	 */
	public void gotoMatchingBracket() {

		bracketMatcher.gotoMatchingBracket(this);
	}

	/**
	 * Get document text - safe way.
	 * 
	 * @return string, never <code>null</code>
	 */
	String getDocumentText() {
		IDocument doc = getDocument();
		if (doc == null) {
			return "";
		}
		return doc.get();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		setDocumentProvider(createDocumentProvider(input));
		super.doSetInput(input);

		rebuildOutline();
	}

	@Override
	protected void editorSaved() {
		super.editorSaved();
		rebuildOutline();
	}

	public void rebuildOutline() {
		String text = getDocumentText();
		
		IPreferenceStore store = BashEditorUtil.getPreferences().getPreferenceStore();

		final boolean validateBlocks=store.getBoolean(VALIDATE_BLOCK_STATEMENTS.getId());
		final boolean validateDo=store.getBoolean(VALIDATE_DO_STATEMENTS.getId());
		final boolean validateIf=store.getBoolean(VALIDATE_IF_STATEMENTS.getId());
		final boolean validateFunctions=store.getBoolean(VALIDATE_IF_STATEMENTS.getId());

		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				BashEditorUtil.removeScriptErrors(BashEditor.this);
				
				modelBuilder.setIgnoreBlockValidation(! validateBlocks);
				modelBuilder.setIgnoreDoValidation(! validateDo);
				modelBuilder.setIgnoreIfValidation(! validateIf);
				modelBuilder.setIgnoreFunctionValidation(! validateFunctions);
				
				BashScriptModel model = modelBuilder.build(text);

				getOutlinePage().rebuild(model);

				if (model.hasErrors()) {
					addErrorMarkers(model);
				}
			}
		});
	}

	/**
	 * Set initial title image dependent on current marker severity. This will
	 * mark error icon on startup time which is not handled by resource change
	 * handling, because having no change...
	 */
	private void setTitleImageInitial() {
		IResource resource = resolveResource();
		if (resource != null) {
			try {
				int maxSeverity = resource.findMaxProblemSeverity(null, true, IResource.DEPTH_INFINITE);
				setTitleImageDependingOnSeverity(maxSeverity);
			} catch (CoreException e) {
				/* ignore */
			}
		}
	}

	/**
	 * Resolves resource from current editor input.
	 * 
	 * @return file resource or <code>null</code>
	 */
	private IResource resolveResource() {
		IEditorInput input = getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		return ((IFileEditorInput) input).getFile();
	}

	private boolean isMarkerChangeForThisEditor(IResourceChangeEvent event) {
		IResource resource = ResourceUtil.getResource(getEditorInput());
		if (resource == null) {
			return false;
		}
		IPath path = resource.getFullPath();
		if (path == null) {
			return false;
		}
		IResourceDelta eventDelta = event.getDelta();
		if (eventDelta == null) {
			return false;
		}
		IResourceDelta delta = eventDelta.findMember(path);
		if (delta == null) {
			return false;
		}
		boolean isMarkerChangeForThisResource = (delta.getFlags() & IResourceDelta.MARKERS) != 0;
		return isMarkerChangeForThisResource;
	}

	private IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new BashTextFileDocumentProvider();
		} else {
			return new BashFileDocumentProvider();
		}
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		if (site == null) {
			return;
		}
		IWorkbenchPage page = site.getPage();
		if (page == null) {
			return;
		}

		// workaround to show action set for block mode etc.
		// https://www.eclipse.org/forums/index.php/t/366630/
		page.showActionSet("org.eclipse.ui.edit.text.actionSet.presentation");

	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	private void activateBashEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext(EDITOR_CONTEXT_MENU_ID);
		}
	}

	private ColorManager getColorManager() {
		return BashEditorActivator.getDefault().getColorManager();
	}

	public void handleColorSettingsChanged() {
		// done like in TextEditor for spelling
		ISourceViewer viewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		if (viewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 viewerExtension2 = (ISourceViewerExtension2) viewer;
			viewerExtension2.unconfigure();
			if (configuration instanceof BashSourceViewerConfiguration) {
				BashSourceViewerConfiguration gconf = (BashSourceViewerConfiguration) configuration;
				gconf.updateTextScannerDefaultColorToken();
			}
			viewer.configure(configuration);
		}
	}

	/**
	 * Toggles comment of current selected lines
	 */
	public void toggleComment() {
		ISelection selection = getSelectionProvider().getSelection();
		if (!(selection instanceof TextSelection)) {
			return;
		}
		IDocumentProvider dp = getDocumentProvider();
		IDocument doc = dp.getDocument(getEditorInput());
		TextSelection ts = (TextSelection) selection;
		int startLine = ts.getStartLine();
		int endLine = ts.getEndLine();

		/* do comment /uncomment */
		for (int i = startLine; i <= endLine; i++) {
			IRegion info;
			try {
				info = doc.getLineInformation(i);
				int offset = info.getOffset();
				String line = doc.get(info.getOffset(), info.getLength());
				StringBuilder foundCode = new StringBuilder();
				StringBuilder whitespaces = new StringBuilder();
				for (int j = 0; j < line.length(); j++) {
					char ch = line.charAt(j);
					if (Character.isWhitespace(ch)) {
						if (foundCode.length() == 0) {
							whitespaces.append(ch);
						}
					} else {
						foundCode.append(ch);
					}
					if (foundCode.length() > 0) {
						break;
					}
				}
				int whitespaceOffsetAdd = whitespaces.length();
				if ("#".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 1, "");
				} else {
					/* not commented */
					doc.replace(offset, 0, "#");
				}

			} catch (BadLocationException e) {
				/* ignore and continue */
				continue;
			}

		}
		/* reselect */
		int selectionStartOffset;
		try {
			selectionStartOffset = doc.getLineOffset(startLine);
			int endlineOffset = doc.getLineOffset(endLine);
			int endlineLength = doc.getLineLength(endLine);
			int endlineLastPartOffset = endlineOffset + endlineLength;
			int length = endlineLastPartOffset - selectionStartOffset;

			ISelection newSelection = new TextSelection(selectionStartOffset, length);
			getSelectionProvider().setSelection(newSelection);
		} catch (BadLocationException e) {
			/* ignore */
		}
	}

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				if (length == 0) {
					/* fall back */
					length = 1;
				}
				selectAndReveal(offset, length);
				if (grabFocus) {
					setFocus();
				}
			}
		}
	}

}
