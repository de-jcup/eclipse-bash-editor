package de.jcup.basheditor.preferences;
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

import static de.jcup.basheditor.BashEditorUtil.getPreferences;
import static de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy.PROJECT;
import static de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy.SCRIPT;
import static de.jcup.basheditor.preferences.BashEditorLinkFunctionStrategy.WORKSPACE;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_AMOUNT_OF_SPACES_FOR_TAB_REPLACEMENT;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_CODE_ASSIST_ADD_KEYWORDS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_CODE_ASSIST_ADD_SIMPLEWORDS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_AUTO_CREATE_END_BRACKETSY;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_ENCLOSING_BRACKETS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_COLOR;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_LINK_FUNCTIONS_STRATEGY;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_LINK_OUTLINE_WITH_EDITOR;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_REPLACE_TAB_BY_SPACES_STRATEGY;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SHARED_MODEL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SHOW_VARIABLES_IN_OUTLINE;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SORT_OUTLINE_ALPHABETICAL;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_TOOLTIPS_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorTabReplaceStrategy.ALWAYS;
import static de.jcup.basheditor.preferences.BashEditorTabReplaceStrategy.NEVER;
import static de.jcup.basheditor.preferences.BashEditorTabReplaceStrategy.USE_DEFAULT;

import java.util.Arrays;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.eclipse.commons.ui.UIMasterSlaveSupport;
import de.jcup.eclipse.commons.ui.preferences.AccessibleBooleanFieldEditor;
import de.jcup.eclipse.commons.ui.preferences.ChangeableComboFieldEditor;

/**
 * Parts are inspired by <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
 * </a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class BashEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
   

    private Button bracketHighlightingCheckbox;
    private Button enclosingBracketsRadioButton;
    private Button matchingBracketAndCaretLocationRadioButton;
    private Button matchingBracketRadioButton;

    private ColorFieldEditor matchingBracketsColor;
    private BooleanFieldEditor linkEditorWithOutline;

    private boolean enclosingBrackets;
    private boolean highlightBracketAtCaretLocation;
    private boolean matchingBrackets;
    private BooleanFieldEditor autoCreateEndBrackets;
    private BooleanFieldEditor codeAssistWithBashKeywords;
    private BooleanFieldEditor codeAssistWithSimpleWords;
    private BooleanFieldEditor toolTipsEnabled;
    private BooleanFieldEditor showVariablesInOutline;
    private UIMasterSlaveSupport uiMasterSlaveSupport;
    private AccessibleBooleanFieldEditor sharedModelBuildEnabled;
    private boolean sharedModelBuildEnabledBefore;
    private BooleanFieldEditor sortOutlineAlphabetical;

    public BashEditorPreferencePage() {
        super(GRID);
        setPreferenceStore(getPreferences().getPreferenceStore());
        uiMasterSlaveSupport=new UIMasterSlaveSupport();
    }

    @Override
    public void init(IWorkbench workbench) {
        this.sharedModelBuildEnabledBefore=BashEditorPreferences.getInstance().isSharedModelBuildEnabled();
    }

    @Override
    public void performDefaults() {
        super.performDefaults();
        reloadBracketHighlightingPreferenceDefaults();
    }

    @Override
    public boolean performOk() {
        boolean ok = super.performOk();
        if (ok) {
            setBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED, matchingBrackets);
            setBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION, highlightBracketAtCaretLocation);
            setBoolean(P_EDITOR_ENCLOSING_BRACKETS, enclosingBrackets);
            
            /* check */
            if (!sharedModelBuildEnabledBefore) {
                if (sharedModelBuildEnabled.getBooleanValue()) {
                    /* means enabled again... we must rebuild the complete model */
                    BashEditorActivator.getDefault().rebuildSharedBashModel();
                }
            }
            
        } 
        return ok;
    }

    

    @Override
    protected void createFieldEditors() {
        Composite appearanceComposite = new Composite(getFieldEditorParent(), SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        appearanceComposite.setLayout(layout);

        /* OTHER */
        Composite otherComposite = new Composite(appearanceComposite, SWT.NONE);
        GridLayout otherLayout = new GridLayout();
        otherLayout.marginWidth = 0;
        otherLayout.marginHeight = 0;
        otherComposite.setLayout(otherLayout);

        /* linking with outline */
        linkEditorWithOutline = new BooleanFieldEditor(P_LINK_OUTLINE_WITH_EDITOR.getId(), "New opened editors are linked with outline", otherComposite);
        linkEditorWithOutline.getDescriptionControl(otherComposite).setToolTipText("Via this setting the default behaviour for new opened outlines is set");
        addField(linkEditorWithOutline);
        
        /* enable variables inside putline */
        showVariablesInOutline = new BooleanFieldEditor(P_SHOW_VARIABLES_IN_OUTLINE.getId(), "Variables are shown in outline", otherComposite);
        showVariablesInOutline.getDescriptionControl(otherComposite).setToolTipText("This defines the behaviour of outline in new opened editors.\nYou have to close and reopen existing files by bash editor to get the effect there as well.");
        addField(showVariablesInOutline);
        
        /* enable sort outline */
        sortOutlineAlphabetical= new BooleanFieldEditor(P_SORT_OUTLINE_ALPHABETICAL.getId(), "New opened outlines have alphabetical sort enabled", otherComposite);
        sortOutlineAlphabetical.getDescriptionControl(otherComposite).setToolTipText("This defines the behaviour of outline in new opened outlines.\nYou have to close and reopen existing files by bash editor to get the effect there as well.");
        addField(sortOutlineAlphabetical);

        Label spacer = new Label(appearanceComposite, SWT.LEFT);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.heightHint = convertHeightInCharsToPixels(1) / 2;
        spacer.setLayoutData(gd);

        /* BRACKETS */
        /*
         * Why so ugly implemented and not using field editors ? Because
         * SourceViewerDecorationSupport needs 3 different preference keys to do its
         * job, so this preference doing must be same as on Java editor preferences.
         */
        GridData bracketsGroupLayoutData = new GridData();
        bracketsGroupLayoutData.horizontalSpan = 2;
        bracketsGroupLayoutData.widthHint = 400;

        Group bracketsGroup = new Group(appearanceComposite, SWT.NONE);
        bracketsGroup.setText("Brackets");
        bracketsGroup.setLayout(new GridLayout());
        bracketsGroup.setLayoutData(bracketsGroupLayoutData);

        autoCreateEndBrackets = new BooleanFieldEditor(P_EDITOR_AUTO_CREATE_END_BRACKETSY.getId(), "Auto create ending brackets", bracketsGroup);
        addField(autoCreateEndBrackets);

        String label = "Bracket highlighting";

        bracketHighlightingCheckbox = addButton(bracketsGroup, SWT.CHECK, label, 0, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                matchingBrackets = bracketHighlightingCheckbox.getSelection();
            }
        });

        Composite radioComposite = new Composite(bracketsGroup, SWT.NONE);
        GridLayout radioLayout = new GridLayout();
        radioLayout.marginWidth = 0;
        radioLayout.marginHeight = 0;
        radioComposite.setLayout(radioLayout);

        label = "highlight matching bracket";
        matchingBracketRadioButton = addButton(radioComposite, SWT.RADIO, label, 0, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (matchingBracketRadioButton.getSelection()) {
                    highlightBracketAtCaretLocation = false;
                }
            }
        });
        uiMasterSlaveSupport.createDependency(bracketHighlightingCheckbox, matchingBracketRadioButton);

        label = "highlight matching bracket and caret location";
        matchingBracketAndCaretLocationRadioButton = addButton(radioComposite, SWT.RADIO, label, 0, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (matchingBracketAndCaretLocationRadioButton.getSelection()) {
                    highlightBracketAtCaretLocation = true;
                }
            }
        });
        uiMasterSlaveSupport.createDependency(bracketHighlightingCheckbox, matchingBracketAndCaretLocationRadioButton);

        label = "highlight enclosing brackets";
        enclosingBracketsRadioButton = addButton(radioComposite, SWT.RADIO, label, 0, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = enclosingBracketsRadioButton.getSelection();
                enclosingBrackets = selection;
                if (selection) {
                    highlightBracketAtCaretLocation = true;
                }
            }
        });
        uiMasterSlaveSupport.createDependency(bracketHighlightingCheckbox, enclosingBracketsRadioButton);

        matchingBracketsColor = new ColorFieldEditor(P_EDITOR_MATCHING_BRACKETS_COLOR.getId(), "Matching brackets color", radioComposite);
        addField(matchingBracketsColor);
        uiMasterSlaveSupport.createDependency(bracketHighlightingCheckbox, matchingBracketsColor.getLabelControl(radioComposite));
        uiMasterSlaveSupport.createDependency(bracketHighlightingCheckbox, matchingBracketsColor.getColorSelector().getButton());

        /* --------------------- */
        /* -- Code assistance -- */
        /* --------------------- */

        GridData codeAssistGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        codeAssistGroupLayoutData.horizontalSpan = 2;
        codeAssistGroupLayoutData.widthHint = 400;

        Group codeAssistGroup = new Group(appearanceComposite, SWT.NONE);
        codeAssistGroup.setText("Code assistance");
        codeAssistGroup.setLayout(new GridLayout());
        codeAssistGroup.setLayoutData(codeAssistGroupLayoutData);

        codeAssistWithBashKeywords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_KEYWORDS.getId(), "Bash and GNU keywords", codeAssistGroup);
        codeAssistWithBashKeywords.getDescriptionControl(codeAssistGroup)
                .setToolTipText("When enabled the standard keywords supported by bash editor are always automatically available as code proposals");
        addField(codeAssistWithBashKeywords);

        codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), "Existing words", codeAssistGroup);
        codeAssistWithSimpleWords.getDescriptionControl(codeAssistGroup)
                .setToolTipText("When enaprotected static final int INDENT = 20;bled the current source will be scanned for words. The existing words will be available as code proposals");
        addField(codeAssistWithSimpleWords);
        toolTipsEnabled = new BooleanFieldEditor(P_TOOLTIPS_ENABLED.getId(), "Tooltips for keywords", codeAssistGroup);
        toolTipsEnabled.getDescriptionControl(codeAssistGroup).setToolTipText("When enabled tool tips will occure for keywords");
        addField(toolTipsEnabled);

        /* --------------------- */
        /* -- Tab replacement -- */
        /* --------------------- */

        GridData tabReplacementGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        tabReplacementGroupLayoutData.horizontalSpan = 2;
        codeAssistGroupLayoutData.widthHint = 400;
        Group tabReplaceGroup = new Group(appearanceComposite, SWT.NONE);
        tabReplaceGroup.setText("Tab replacement");
        tabReplaceGroup.setLayout(new GridLayout());
        tabReplaceGroup.setLayoutData(tabReplacementGroupLayoutData);

        String[][] labelAndValues = new String[][] { new String[] { NEVER.getLabelText(), NEVER.getId() }, new String[] { USE_DEFAULT.getLabelText(), USE_DEFAULT.getId() },
                new String[] { ALWAYS.getLabelText(), ALWAYS.getId() }, };
        ChangeableComboFieldEditor tabReplaceStrategy = new ChangeableComboFieldEditor(P_REPLACE_TAB_BY_SPACES_STRATEGY.getId(), "Replace strategy", labelAndValues, tabReplaceGroup) ;
        addField(tabReplaceStrategy);
        
        IntegerFieldEditor amountFieldEditor = new IntegerFieldEditor(P_AMOUNT_OF_SPACES_FOR_TAB_REPLACEMENT.getId(), "Amount of spaces", tabReplaceGroup);
        amountFieldEditor.setValidRange(2, 20);
        addField(amountFieldEditor);
        uiMasterSlaveSupport.createDependency(tabReplaceStrategy.getComboBoxControl(tabReplaceGroup),amountFieldEditor.getTextControl(tabReplaceGroup), Arrays.asList(ALWAYS.getLabelText()));
        
        
        /* ------------------------ */
        /* -- Shared model setup -- */
        /* ------------------------ */
        sharedModelBuildEnabled = new AccessibleBooleanFieldEditor(P_SHARED_MODEL_ENABLED.getId(), "Shared model build enabled", appearanceComposite);
        sharedModelBuildEnabled.getDescriptionControl(appearanceComposite).setToolTipText(
                  "When enabled a bash model is build which contains all bash script models among complete workspace.\n"
                + "\n"
                + "This will make some special features available: E.g. hyperlinking to external defined functions.\n"
                + "If you have performance problems because of if this model build\n"
                + "you can disable it here.\n");
        
        addField(sharedModelBuildEnabled);
        
        GridData sharedModelGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        sharedModelGroupLayoutData.horizontalSpan = 2;
        codeAssistGroupLayoutData.widthHint = 400;
        Group sharedModelGroup = new Group(appearanceComposite, SWT.NONE);
        sharedModelGroup.setText("Shared model");
        sharedModelGroup.setLayout(new GridLayout());
        sharedModelGroup.setLayoutData(sharedModelGroupLayoutData);

        String[][] linkStrategyLabelAndValues = new String[][] { new String[] { SCRIPT.getLabelText(), SCRIPT.getId() }, new String[] { PROJECT.getLabelText(), PROJECT.getId() },
                new String[] { WORKSPACE.getLabelText(), WORKSPACE.getId() }, };
        ChangeableComboFieldEditor linkStrategy = new ChangeableComboFieldEditor(P_LINK_FUNCTIONS_STRATEGY.getId(), "Function hyperlink strategy", linkStrategyLabelAndValues, sharedModelGroup) ;
        linkStrategy.getLabelControl(sharedModelGroup).setToolTipText("This defines how hyperlinking in functions works - means potential targets can be resolved.");
        addField(linkStrategy);
        uiMasterSlaveSupport.createDependency(sharedModelBuildEnabled.getChangeControl(appearanceComposite),sharedModelGroup);
        uiMasterSlaveSupport.updateSlaveComponents();
    }

    @Override
    protected void initialize() {
        initializeBracketHighlightingPreferences();
        super.initialize();
        uiMasterSlaveSupport.updateSlaveComponents();
    }
    private Button addButton(Composite parent, int style, String label, int indentation, SelectionListener listener) {
        Button button = new Button(parent, style);
        button.setText(label);

        GridData gd = new GridData(32);
        gd.horizontalIndent = indentation;
        gd.horizontalSpan = 2;
        button.setLayoutData(gd);
        button.addSelectionListener(listener);

        return button;
    }

    private void setBoolean(BashEditorPreferenceConstants id, boolean value) {
        getPreferences().setBooleanPreference(id, value);
    }

    private boolean getBoolean(BashEditorPreferenceConstants id) {
        return getPreferences().getBooleanPreference(id);
    }

    private boolean getDefaultBoolean(BashEditorPreferenceConstants id) {
        return getPreferences().getDefaultBooleanPreference(id);
    }protected static final int INDENT = 20;

    private void initializeBracketHighlightingPreferences() {
        matchingBrackets = getBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED);
        highlightBracketAtCaretLocation = getBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION);
        enclosingBrackets = getBoolean(P_EDITOR_ENCLOSING_BRACKETS);

        updateBracketUI();
    }

    private void reloadBracketHighlightingPreferenceDefaults() {
        matchingBrackets = getDefaultBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED);
        highlightBracketAtCaretLocation = getDefaultBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION);
        enclosingBrackets = getDefaultBoolean(P_EDITOR_ENCLOSING_BRACKETS);

        updateBracketUI();
    }

    private void updateBracketUI() {
        this.bracketHighlightingCheckbox.setSelection(matchingBrackets);

        this.enclosingBracketsRadioButton.setSelection(enclosingBrackets);
        if (!(enclosingBrackets)) {
            this.matchingBracketRadioButton.setSelection(!(highlightBracketAtCaretLocation));
            this.matchingBracketAndCaretLocationRadioButton.setSelection(highlightBracketAtCaretLocation);
        }
        uiMasterSlaveSupport.updateSlaveComponents();
    }

   
}
