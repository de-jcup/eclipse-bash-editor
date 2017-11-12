package de.jcup.basheditor;

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;

import java.util.ArrayList;
import java.util.List;
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
import de.jcup.basheditor.preferences.BashEditorPreferences;

public class BashEditorSimpleWordContentAssistProcessor implements IContentAssistProcessor, ICompletionListener {

	private static final SimpleWordListBuilder WORD_LIST_BUILDER = new SimpleWordListBuilder();
	private static final NoWordListBuilder NO_WORD_BUILDER = new NoWordListBuilder();

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
			int zeroOffset = offset-textBefore.length();
			try {
				document.replace(zeroOffset, textBefore.length(), proposal);
				nextSelection = zeroOffset + proposal.length();
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
		
		BashEditorPreferences preferences = BashEditorPreferences.getInstance();
		boolean addKeyWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_KEYWORDS);
		boolean addSimpleWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_SIMPLEWORDS);
		
		if (addSimpleWords){
			simpleWordCompletion.setWordListBuilder(WORD_LIST_BUILDER);
		}else{
			simpleWordCompletion.setWordListBuilder(NO_WORD_BUILDER);
		}
		if (addKeyWords){
			addAllBashKeyWords();
		}
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
		simpleWordCompletion.reset();// clean up...
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {

	}

	private static class NoWordListBuilder implements WordListBuilder{

		private NoWordListBuilder(){
			
		}
		private List<String> list = new ArrayList<>(0);

		@Override
		public List<String> build(String source) {
			return list;
		}
		
	}
}
