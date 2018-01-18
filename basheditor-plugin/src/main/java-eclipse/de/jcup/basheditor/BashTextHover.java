package de.jcup.basheditor;

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;

import de.jcup.basheditor.document.keywords.DocumentKeyWord;
import de.jcup.basheditor.document.keywords.DocumentKeyWords;
import de.jcup.basheditor.preferences.BashEditorPreferences;

public class BashTextHover implements ITextHover, ITextHoverExtension {

	private IInformationControlCreator creator;

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (creator == null) {
			creator = new GradleTextHoverControlCreator();
		}
		return creator;
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		BashEditorPreferences preferences = BashEditorPreferences.getInstance();
		boolean tooltipsEnabled = preferences.getBooleanPreference(P_TOOLTIPS_ENABLED);
		if (!tooltipsEnabled){
			return null;
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
		String word = SimpleStringUtils.nextWordUntilWhitespace(text, offset);
		if (word.isEmpty()) {
			return "";
		}

		for (DocumentKeyWord keyword : DocumentKeyWords.getAll()) {
			if (word.equals(keyword.getText())) {
				return buildHoverInfo(keyword);
			}
		}

		return "";
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
		addCSStoMakePreWrappingWhenTooLong(sb);
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		if (!isEmpty(link)) {
			sb.append("Detailed information available at: <a href='" + link + "' target='_blank'>" + link
					+ "</a><br><br>");
		}
		sb.append("<u>Offline description:</u>");
		sb.append("<pre style='font-size:small'>");
		if (isEmpty(tooltip)) {
			sb.append("Not available");
		} else {
			sb.append(tooltip);
		}
		sb.append("</pre>");
		sb.append("</body>");
		return sb.toString();
	}

	protected void addCSStoMakePreWrappingWhenTooLong(StringBuilder sb) {
		sb.append("pre {");
		sb.append("white-space: pre-wrap;       /* Since CSS 2.1 */");
		sb.append("white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */");
		sb.append("white-space: -pre-wrap;      /* Opera 4-6 */");
		sb.append("white-space: -o-pre-wrap;    /* Opera 7 */");
		sb.append("word-wrap: break-word;       /* Internet Explorer 5.5+ */");
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
