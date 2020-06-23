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

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_ENCLOSING_BRACKETS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_COLOR;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_BLOCK_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_DO_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_ERROR_LEVEL;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_FUNCTION_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_IF_STATEMENTS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
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
import de.jcup.basheditor.outline.BashEditorTreeContentProvider;
import de.jcup.basheditor.outline.BashQuickOutlineDialog;
import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.preferences.BashEditorTabReplaceStrategy;
import de.jcup.basheditor.process.BashEditorFileProcessContext;
import de.jcup.basheditor.process.CancelStateProvider;
import de.jcup.basheditor.process.OutputHandler;
import de.jcup.basheditor.process.SimpleProcessExecutor;
import de.jcup.basheditor.process.TimeStampChangedEnforcer;
import de.jcup.basheditor.script.BashError;
import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.basheditor.script.BashScriptModelBuilder;
import de.jcup.basheditor.script.BashScriptModelException;
import de.jcup.basheditor.script.parser.validator.BashEditorValidationErrorLevel;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.replacetabbyspaces.ReplaceTabBySpacesProvider;
import de.jcup.eclipse.commons.replacetabbyspaces.ReplaceTabBySpacesSupport;

@AdaptedFromEGradle
public class BashEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

    /** The COMMAND_ID of this editor as defined in plugin.xml */
    public static final String EDITOR_ID = "basheditor.editors.BashEditor";
    /** The COMMAND_ID of the editor context menu */
    public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
    /** The COMMAND_ID of the editor ruler context menu */
    public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";
    /** Max execution time for external tool to run on save action */
    public static final int EXTERNAL_TOOL_TIMEOUT_ON_SAVE_SECS = 10;
    
    private static final BashTextFileDocumentProvider SHARED_TEXTFILE_DOCUMENT_PROVIDER=new BashTextFileDocumentProvider();
    private static final BashFileDocumentProvider SHARED_FILE_DOCUMENT_PROVIDER= new BashFileDocumentProvider();


    private ReplaceTabBySpacesSupport replaceTabBySpaceSupport = new ReplaceTabBySpacesSupport();
    private BashBracketsSupport bracketMatcher = new BashBracketsSupport();
    private SourceViewerDecorationSupport additionalSourceViewerSupport;
    private BashEditorContentOutlinePage outlinePage;
    private BashScriptModelBuilder modelBuilder;
    private Object monitor = new Object();
    private boolean quickOutlineOpened;
    private int lastCaretPosition;
    private static final BashScriptModel FALLBACK_MODEL = new BashScriptModel();
    private ExternalToolCommandArrayBuilder commandArrayBuilder = new ExternalToolCommandArrayBuilder();
    private TimeStampChangedEnforcer timestampChangeEnforder = new TimeStampChangedEnforcer();

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

    /**
     * Opens quick outline
     */
    public void openQuickOutline() {
        synchronized (monitor) {
            if (quickOutlineOpened) {
                /*
                 * already opened - this is in future the anker point#117 for ctrl+o+o...
                 */
                return;
            }
            quickOutlineOpened = true;
        }
        Shell shell = getEditorSite().getShell();
        BashScriptModel model = buildModelWithoutValidation();
        BashQuickOutlineDialog dialog = new BashQuickOutlineDialog(this, shell, "Quick outline");
        dialog.setInput(model);

        dialog.open();
        synchronized (monitor) {
            quickOutlineOpened = false;
        }
    }

    private BashScriptModel buildModelWithoutValidation() {
        String text = getDocumentText();

        /* for quick outline create own model and ignore any validations */
        modelBuilder.setIgnoreBlockValidation(true);
        modelBuilder.setIgnoreDoValidation(true);
        modelBuilder.setIgnoreIfValidation(true);
        modelBuilder.setIgnoreFunctionValidation(true);

        BashScriptModel model;
        try {
            model = modelBuilder.build(text);
        } catch (BashScriptModelException e) {
            BashEditorUtil.logError("Was not able to build script model", e);
            model = FALLBACK_MODEL;
        }
        return model;
    }

    void setTitleImageDependingOnSeverity(int severity) {
        EclipseUtil.safeAsyncExec(() -> setTitleImage(EclipseUtil.getImage("icons/" + getTitleImageName(severity), BashEditorActivator.PLUGIN_ID)));
    }

    private String getTitleImageName(int severity) {
        switch (severity) {
        case IMarker.SEVERITY_ERROR:
            return "bash-editor-with-error.png";
        case IMarker.SEVERITY_WARNING:
            return "bash-editor-with-warning.png";
        case IMarker.SEVERITY_INFO:
            return "bash-editor-with-info.png";
        default:
            return "bash-editor.png";
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

    private void addErrorMarkers(BashScriptModel model, int severity) {
        if (model == null) {
            return;
        }
        IDocument document = getDocument();
        if (document == null) {
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
            BashEditorUtil.addScriptError(this, line, error, severity);
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

        Control adapter = getAdapter(Control.class);
        if (adapter instanceof StyledText) {
            StyledText text = (StyledText) adapter;
            text.addCaretListener(new BashEditorCaretListener());
        }

        activateBashEditorContext();

        installAdditionalSourceViewerSupport();

        StyledText styledText = getSourceViewer().getTextWidget();
        styledText.addKeyListener(new BashBracketInsertionCompleter(this));

        /*
         * register as resource change listener to provide marker change listening
         */
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        setTitleImageInitial();

        replaceTabBySpaceSupport.install(this, new ReplaceTabBySpacesProvider() {

            @Override
            public boolean isReplaceTabBySpacesEnabled() {
                BashEditorTabReplaceStrategy strategy = BashEditorPreferences.getInstance().getReplaceTabBySpacesStrategy();
                if (BashEditorTabReplaceStrategy.ALWAYS.equals(strategy)) {
                    return true;
                }
                /*
                 * in all other cases we do NOT use the replacement implementation. Why ? We let
                 * the standard text editor implementation work on default setup
                 */
                return false;
            }

            @Override
            public PluginContextProvider getPluginContextProvider() {
                return BashEditorActivator.getDefault();
            }

            @Override
            public int getAmountOfSpacesToReplaceTab() {
                return BashEditorPreferences.getInstance().getAmountOfSpacesToReplaceTab();
            }
        });
    }

    @Override
    protected boolean isTabsToSpacesConversionEnabled() {
        BashEditorTabReplaceStrategy strategy = BashEditorPreferences.getInstance().getReplaceTabBySpacesStrategy();
        if (BashEditorTabReplaceStrategy.USE_DEFAULT.equals(strategy)) {
            return super.isTabsToSpacesConversionEnabled();
        }
        return false;
    }

    public BashEditorContentOutlinePage getOutlinePage() {
        if (outlinePage == null) {
            outlinePage = new BashEditorContentOutlinePage(this);
        }
        return outlinePage;
    }

    /**
     * #117 Installs an additional source viewer support which uses editor
     * preferences instead of standard text preferences. If standard source viewer
     * support would be set with editor preferences all standard preferences would
     * be lost or had to be reimplmented. To avoid this another source viewer
     * support is installed...
     */
    private void installAdditionalSourceViewerSupport() {

        additionalSourceViewerSupport = new SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(), getAnnotationAccess(), getSharedColors());
        additionalSourceViewerSupport.setCharacterPairMatcher(bracketMatcher);
        additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
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
             * TODO ATR, 03.02.2017: there should be an easier approach to get editors back
             * and foreground, without syncexec
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
    private boolean ignoreNextCaretMove;
    private boolean lastModelBuildHadErrors;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (BashEditor.class.equals(adapter)) {
            return (T) this;
        }
        if (ITextViewer.class.equals(adapter)){
        	/* fall back for Eclipse neon working - AbstractTextEditor did not support his in neon, so adapter was always null... see #162 */
        	return (T)getSourceViewer();
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
        if (IProject.class.equals(adapter)) {
            IEditorInput input = getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput feditorInput = (IFileEditorInput) input;
                return (T)feditorInput.getFile().getProject();
            }
        }
        
        if (ISourceViewer.class.equals(adapter)) {
            return (T) getSourceViewer();
        }
        if (StatusMessageSupport.class.equals(adapter)) {
            return (T) this;
        }
        if (ITreeContentProvider.class.equals(adapter) || BashEditorTreeContentProvider.class.equals(adapter)) {
            if (outlinePage == null) {
                return null;
            }
            return (T) outlinePage.getContentProvider();
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
        setDocumentProvider(resolveSharedDocumentProvider(input));
        super.doSetInput(input);

        rebuildOutline();
    }

    @Override
    protected void editorSaved() {
        super.editorSaved();
        rebuildOutline();
    }

    /**
     * Does rebuild the outline - this is done asynchronous
     */
    public void rebuildOutline() {
        lastModelBuildHadErrors = false;

        String text = getDocumentText();

        IPreferenceStore store = BashEditorUtil.getPreferences().getPreferenceStore();

        boolean validateBlocks = store.getBoolean(VALIDATE_BLOCK_STATEMENTS.getId());
        boolean validateDo = store.getBoolean(VALIDATE_DO_STATEMENTS.getId());
        boolean validateIf = store.getBoolean(VALIDATE_IF_STATEMENTS.getId());
        boolean validateFunctions = store.getBoolean(VALIDATE_FUNCTION_STATEMENTS.getId());
        String errorLevelId = store.getString(VALIDATE_ERROR_LEVEL.getId());
        BashEditorValidationErrorLevel errorLevel = BashEditorValidationErrorLevel.fromId(errorLevelId);

        boolean debugMode = Boolean.parseBoolean(System.getProperty("basheditor.debug.enabled"));

        modelBuilder.setIgnoreBlockValidation(!validateBlocks);
        modelBuilder.setIgnoreDoValidation(!validateDo);
        modelBuilder.setIgnoreIfValidation(!validateIf);
        modelBuilder.setIgnoreFunctionValidation(!validateFunctions);

        modelBuilder.setDebug(debugMode);

        EclipseUtil.safeAsyncExec(new Runnable() {

            @Override
            public void run() {
                BashEditorUtil.removeScriptErrors(BashEditor.this);

                BashScriptModel model;
                try {
                    modelBuilder.setIgnoreVariables(!BashEditorPreferences.getInstance().isOutlineShowVariablesEnabled());

                    model = modelBuilder.build(text);
                } catch (BashScriptModelException e) {
                    BashEditorUtil.logError("Was not able to build validation model", e);
                    model = FALLBACK_MODEL;
                }

                getOutlinePage().rebuild(model);

                if (model.hasErrors()) {
                    lastModelBuildHadErrors = true;
                    int severity;
                    if (BashEditorValidationErrorLevel.INFO.equals(errorLevel)) {
                        severity = IMarker.SEVERITY_INFO;
                    } else if (BashEditorValidationErrorLevel.WARNING.equals(errorLevel)) {
                        severity = IMarker.SEVERITY_WARNING;
                    } else {
                        severity = IMarker.SEVERITY_ERROR;
                    }
                    addErrorMarkers(model, severity);
                }
            }
        });
    }

    /**
     * Set initial title image dependent on current marker severity. This will mark
     * error icon on startup time which is not handled by resource change handling,
     * because having no change...
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
    private IFile resolveResourceAsIFile() {
        IEditorInput input = getEditorInput();
        if (!(input instanceof IFileEditorInput)) {
            return null;
        }
        return ((IFileEditorInput) input).getFile();
    }

    private File resolveResourceAsFile() throws CoreException {
        IFile ifile = resolveResourceAsIFile();
        IPath location = ifile.getLocation();
        if (location == null) {
            return null;
        }
        IFileStore fileStore = FileBuffers.getFileStoreAtLocation(location);

        File file = fileStore.toLocalFile(EFS.NONE, null);
        return file;
    }

    private IResource resolveResource() {
        return resolveResourceAsIFile();
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
    
    private IDocumentProvider resolveSharedDocumentProvider(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            return SHARED_TEXTFILE_DOCUMENT_PROVIDER;
        } else {
            return SHARED_FILE_DOCUMENT_PROVIDER;
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

//		DocumentLineChangeSupport lineChangeSupport = new DocumentLineChangeSupport();
//		try {
//			lineChangeSupport.register(this);
//		} catch (CoreException e) {
//			EclipseUtil.logError("Wasn't able to register line change support", e);
//		}

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
                ignoreNextCaretMove = true;
                selectAndReveal(offset, length);
                if (grabFocus) {
                    setFocus();
                }
            }
        }
    }

    public Item getItemAtCarretPosition() {
        return getItemAt(lastCaretPosition);
    }

    public Item getItemAt(int offset) {
        if (outlinePage == null) {
            return null;
        }
        BashEditorTreeContentProvider contentProvider = outlinePage.getContentProvider();
        if (contentProvider == null) {
            return null;
        }
        Item item = contentProvider.tryToFindByOffset(offset);
        return item;
    }

    public void selectFunction(String text) {
        System.out.println("should select functin:" + text);

    }

    public BashFunction findBashFunction(String functionName) {
        if (functionName == null) {
            return null;
        }
        BashScriptModel model = buildModelWithoutValidation();
        Collection<BashFunction> functions = model.getFunctions();
        for (BashFunction function : functions) {
            if (functionName.equals(function.getName())) {
                return function;
            }
        }
        return null;
    }

    public BashEditorPreferences getPreferences() {
        return BashEditorPreferences.getInstance();
    }

    private class BashEditorCaretListener implements CaretListener {

        @Override
        public void caretMoved(CaretEvent event) {
            if (event == null) {
                return;
            }
            lastCaretPosition = event.caretOffset;
            if (ignoreNextCaretMove) {
                ignoreNextCaretMove = false;
                return;
            }
            if (outlinePage == null) {
                return;
            }
            outlinePage.onEditorCaretMoved(event.caretOffset);
        }

    }

    public void validate() {
        rebuildOutline();
    }

    @Override
    protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {

        // first of all do save the changes to disk (without external tool pass):
        // the next method call does saved text and rebuild the internal model +
        // lastModelBuildHadErrors state
        super.performSave(overwrite, progressMonitor);

        if (!isRunningExternalToolOnSave()) {
            return;
        }
        /*
         * execute this after outline build is done async - so also async call necessary
         * to get correct error state
         */
        EclipseUtil.safeAsyncExec(() -> executeExternalActionsIfNoErrors());

    }

    private void executeExternalActionsIfNoErrors() {
        /*
         * we must fetch the result by waiting outline build done (which was done async)
         */
        if (lastModelBuildHadErrors) {
            // when there are internal failures we do NOT call the external tool, because
            // for example beautysh.py would fail again
            // and we will get odd situations on UI
            return;
        }

        /* prevent user interaction at editor while external tool job is running */
        Control control = getAdapter(Control.class);

        EclipseUtil.safeAsyncExec(() -> {
            if (!control.isDisposed()) {
                if (control instanceof StyledText) {
                    StyledText t = (StyledText) control;
                    t.setEditable(false);
                }
            }
        });
        Job job = new Job("execute bash editor save action") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    callExternalToolAndRefreshEditorContent(monitor);
                } catch (CoreException e) {
                    return e.getStatus();
                } finally {
                    /* in any case, enable editor again */
                    EclipseUtil.safeAsyncExec(() -> {
                        if (!control.isDisposed()) {
                            if (control instanceof StyledText) {
                                StyledText t = (StyledText) control;
                                t.setEditable(true);
                            }
                        }
                    });
                }
                return Status.OK_STATUS;
            }
        };

        job.setUser(true);
        job.setSystem(false);
        job.schedule();
    }

    private void callExternalToolAndRefreshEditorContent(IProgressMonitor progressMonitor) throws CoreException {
        // we will run the external tool from the directory where the current file is
        // located:
        String externalToolString = getPreferences().getStringPreference(P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND);
        if (externalToolString == null || externalToolString.trim().isEmpty()) {
            return;
        }

        if (progressMonitor != null) {
            progressMonitor.beginTask(externalToolString, 1);
        }
        try {
            File bashFile = resolveResourceAsFile();
            if (bashFile == null) {
                return; // cannot reformat
            }

            BashEditorFileProcessContext processContext = new BashEditorFileProcessContext(bashFile);
            processContext.setCancelStateProvider(new CancelStateProvider() {

                @Override
                public boolean isCanceled() {
                    if (progressMonitor != null) {
                        return progressMonitor.isCanceled();
                    }
                    return false;
                }
            });

            // substitute in the external tool cmd line the special placeholders:
            String[] cmd_args = commandArrayBuilder.build(externalToolString, bashFile);

            OutputHandler.STRING_OUTPUT.clearOutput();
            SimpleProcessExecutor executor = new SimpleProcessExecutor(OutputHandler.STRING_OUTPUT, true, true, EXTERNAL_TOOL_TIMEOUT_ON_SAVE_SECS);

            /* handle potential cancel operation */
            if (progressMonitor != null) {
                if (progressMonitor.isCanceled()) {
                    return;
                }
            }
            /*
             * we must ensure time stamp will be changed by external - to have
             * refreshLocal() call working...
             */
            timestampChangeEnforder.ensureNextWriteChangesFileStamp(bashFile);

            int exitCode = executor.execute(processContext, processContext, processContext, cmd_args);
            if (exitCode == 0) {
                // reload external tool output:
                // see https://wiki.eclipse.org/FAQ_When_should_I_use_refreshLocal%3F
                IFile file = resolveResourceAsIFile();
                if (file == null) {
                    /* should not happen ... but... */
                    return;
                }
                file.refreshLocal(IResource.DEPTH_ZERO, progressMonitor);
            } else {
                throw new CoreException(new Status(Status.ERROR, BashEditorActivator.PLUGIN_ID,
                        "External re-formatting tool '" + externalToolString + "' failed with exit code " + exitCode + ": " + OutputHandler.STRING_OUTPUT.getFullOutput()));
            }
        } catch (IOException e) {
            throw new CoreException(new Status(Status.ERROR, BashEditorActivator.PLUGIN_ID, "Failed running external re-formatting tool '" + externalToolString + "'", e));
        }
    }

    private boolean isRunningExternalToolOnSave() {
        BashEditorPreferences preferences = BashEditorPreferences.getInstance();
        return preferences.getBooleanPreference(P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED);
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    String writeDocumentInTempFile() throws IOException {
        File tempFile;
        tempFile = File.createTempFile("eclipse-basheditor", ".tmp");

        // put the current contents in the temp file
        FileWriter fw = new FileWriter(tempFile);
        fw.write(getDocumentText());
        fw.close();

        return tempFile.getAbsolutePath();
    }
}
