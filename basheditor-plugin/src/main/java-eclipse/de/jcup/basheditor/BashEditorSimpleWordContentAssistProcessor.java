package de.jcup.basheditor;

import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.jcup.basheditor.document.keywords.BashGnuCommandKeyWords;
import de.jcup.basheditor.document.keywords.BashIncludeKeyWords;
import de.jcup.basheditor.document.keywords.BashLanguageKeyWords;
import de.jcup.basheditor.document.keywords.BashSpecialVariableKeyWords;
import de.jcup.basheditor.document.keywords.BashSystemKeyWords;
import de.jcup.basheditor.document.keywords.DocumentKeyWord;

public class BashEditorSimpleWordContentAssistProcessor implements IContentAssistProcessor, ICompletionListener {

	private String errorMessage;

	private SimpleWordCodeCompletion simpleWordCompletion = new SimpleWordCodeCompletion();

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		if (document == null) {
			return null;
		}
		String source = document.get();

		Set<String> words = simpleWordCompletion.calculate(source, offset);

		ICompletionProposal[] result = new ICompletionProposal[words.size()];
		int i = 0;
		for (String word : words) {
			result[i++] = new SimpleWordProposal(offset, word);
		}

		return result;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	private class SimpleWordProposal implements ICompletionProposal {

		private int offset;
		private String word;
		private int nextSelection;

		SimpleWordProposal(int offset, String word) {
			this.offset = offset;
			this.word = word;
		}

		@Override
		public void apply(IDocument document) {
			// the proposal shall enter always a space after applyment...
			String proposal = word;
			if (isAddingSpaceAtEnd()) {
				proposal += " ";
			}

			String source = document.get();
			String textBefore = simpleWordCompletion.getTextbefore(source, offset);
			String missingPart = proposal.substring(textBefore.length());
			try {
				document.replace(offset, 0, missingPart);
				nextSelection = offset + missingPart.length();
			} catch (BadLocationException e) {
				/* ignore */
			}

		}

		@Override
		public Point getSelection(IDocument document) {
			Point point = new Point(nextSelection, 0);
			return point;
		}

		@Override
		public String getAdditionalProposalInfo() {
			return null;
		}

		@Override
		public String getDisplayString() {
			return word;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public IContextInformation getContextInformation() {
			return null;
		}

	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public boolean isAddingSpaceAtEnd() {
		return true;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public ICompletionListener getCompletionListener() {
		return this;
	}

	/* completion listener parts: */

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		simpleWordCompletion.reset();
		addAllBashKeyWords();
	}

	protected void addAllBashKeyWords() {
		for (DocumentKeyWord keyword : BashGnuCommandKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : BashIncludeKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : BashLanguageKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : BashSpecialVariableKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : BashSystemKeyWords.values()) {
			addKeyWord(keyword);
		}
	}

	protected void addKeyWord(DocumentKeyWord keyword) {
		simpleWordCompletion.add(keyword.getText());
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {

	}

}
