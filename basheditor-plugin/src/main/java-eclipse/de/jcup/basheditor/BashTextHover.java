/*
 * Copyright 2018 Albert Tregnaghi
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

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_TOOLTIPS_ENABLED;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

import de.jcup.basheditor.debug.element.BashDebugTarget;
import de.jcup.basheditor.debug.element.BashStackFrame;
import de.jcup.basheditor.debug.element.BashThread;
import de.jcup.basheditor.debug.element.BashVariable;
import de.jcup.basheditor.document.keywords.DocumentKeyWords;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;
import de.jcup.eclipse.commons.keyword.TooltipTextSupport;
import de.jcup.eclipse.commons.ui.ReducedBrowserInformationControl;

public class BashTextHover implements ITextHover, ITextHoverExtension {

    private IInformationControlCreator creator;
    private String bgColor;
    private String fgColor;
    private String commentColorWeb;
    private IAdaptable adaptable;

    public BashTextHover(IAdaptable adaptable) {
        this.adaptable = adaptable;
    }

    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (creator == null) {
            creator = new GradleTextHoverControlCreator();
        }
        return creator;
    }

    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        if (adaptable == null) {
            return "";
        }
        BashEditor bashEditor = adaptable.getAdapter(BashEditor.class);
        if (bashEditor == null) {
            return "";
        }
        BashStackFrame frameToInspect = searchForFrameWhenDebugTargetsAvailable(bashEditor);

        BashEditorPreferences preferences = BashEditorPreferences.getInstance();
        boolean tooltipsEnabled = frameToInspect != null || preferences.getBooleanPreference(P_TOOLTIPS_ENABLED);
        if (!tooltipsEnabled) {
            return null;
        }

        if (bgColor == null || fgColor == null) {

            StyledText textWidget = textViewer.getTextWidget();
            if (textWidget != null) {

                EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
                        fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
                    }
                });
            }

        }
        if (commentColorWeb == null) {
            commentColorWeb = preferences.getWebColor(BashEditorSyntaxColorPreferenceConstants.COLOR_COMMENT);
        }

        IDocument document = textViewer.getDocument();
        if (document == null) {
            return "";
        }
        String text = document.get();
        if (text == null) {
            return "";
        }
        int offset = hoverRegion.getOffset();
        String word = SimpleStringUtils.nextReducedVariableWord(text, offset);
        if (word.isEmpty()) {
            return "";
        }
        if (word.startsWith("$(")) {
            word=word.substring(2); // cut off - example: "BLACK=$(tput setaf 0)" and user hovers over tput... we have $(tput as word and must handle this
        }

        if (frameToInspect != null) {
            try {
                IVariable[] variables = frameToInspect.getVariables();
                for (IVariable variable : variables) {
                    if (!(variable instanceof BashVariable)) {
                        continue;
                    }
                    BashVariable bashVar = (BashVariable) variable;
                    String varName = bashVar.getName();
                    if (varName == null) {
                        continue;
                    }
                    if (varName.contentEquals(word)) {
                        return varName + "=" + bashVar.getBashValueAsString();
                    }

                }
            } catch (DebugException e) {
                BashEditorUtil.logError("Cannot inspect bash stack frame variables", e);
            }
        }

        for (DocumentKeyWord keyword : DocumentKeyWords.getAll()) {
            if (word.equals(keyword.getText())) {
                return buildHoverInfo(keyword);
            }
        }

        return "";
    }

    private BashStackFrame searchForFrameWhenDebugTargetsAvailable(BashEditor bashEditor) {
        BashStackFrame frameToInspect = null;

        IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
        for (IDebugTarget target : targets) {
            if (target.isTerminated()) {
                continue;
            }

            if (target instanceof BashDebugTarget) {
                BashDebugTarget bashTarget = (BashDebugTarget) target;
                BashThread bashThread = bashTarget.getBashThread();
                if (bashThread == null || bashThread.isTerminated()) {
                    continue;
                }
                try {
                    IStackFrame[] frames = bashThread.getStackFrames();
                    if (frames == null || frames.length == 0) {
                        continue;
                    }
                    /* we have only one */
                    IStackFrame frame = frames[0];
                    BashStackFrame bashFrame = (BashStackFrame) frame;
                    String sourceFilename = bashFrame.getSourceFileName();
                    if (sourceFilename == null) {
                        continue;
                    }
                    IResource resource = bashEditor.resolveResource();
                    if (resource == null) {
                        continue;
                    }
                    /* TODO de-jcup: improve this - currently only check for lastname is done... */
                    String name = resource.getName();// getLocation().toOSString();
                    if (sourceFilename.endsWith(name)) {// contentEquals(name)){
                        frameToInspect = bashFrame;
                        break;
                    }

                } catch (DebugException e) {
                    BashEditorUtil.logError("Cannot inspect bash stack frame", e);
                    continue;
                }
            }
        }
        return frameToInspect;
    }

    private String buildHoverInfo(DocumentKeyWord keyword) {
        String link = keyword.getLinkToDocumentation();
        String tooltip = keyword.getTooltip();

        if (isEmpty(tooltip) && isEmpty(link)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>");
        sb.append(TooltipTextSupport.getTooltipCSS());
        addCSStoBackgroundTheme(sb);
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        if (!isEmpty(link)) {
            sb.append("Detailed information available at: <a href='" + link + "' target='_blank'>" + link + "</a><br><br>");
        }

        sb.append("<u>Offline description:</u>");
        if (isEmpty(tooltip)) {
            sb.append("<b>Not available</b>");
        } else {
            if (TooltipTextSupport.isHTMLToolTip(tooltip)) {
                /* it's already a HTML variantFullText - so just keep as is */
                sb.append(tooltip);
            } else {
                /* plain text */
                sb.append("<pre class='preWrapEnabled'>");
                sb.append(tooltip);
                sb.append("</pre>");
            }

        }
        sb.append("</body>");
        return sb.toString();
    }

    private void addCSStoBackgroundTheme(StringBuilder sb) {
        if (bgColor == null) {
            return;
        }
        if (fgColor == null) {
            return;
        }
        sb.append("body {");
        sb.append("background-color:").append(bgColor).append(";");
        sb.append("color:").append(fgColor).append(";");
        sb.append("}");

    }

    private boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        return string.isEmpty();
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }

    private class GradleTextHoverControlCreator implements IInformationControlCreator {

        @Override
        public IInformationControl createInformationControl(Shell parent) {
            if (ReducedBrowserInformationControl.isAvailableFor(parent)) {
                ReducedBrowserInformationControl control = new ReducedBrowserInformationControl(parent);
                return control;
            } else {
                return new DefaultInformationControl(parent, true);
            }
        }
    }

}
