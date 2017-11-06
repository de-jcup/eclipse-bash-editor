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

public class BashEditorSimpleWordContentAssistProcessor implements IContentAssistProcessor, ICompletionListener{

	private String errorMessage;

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		if (document==null) {
			return null;
		}
		String source = document.get();
		
		SimpleWordCodeCompletion simpleWordCompletion = new SimpleWordCodeCompletion();
		Set<String> words = simpleWordCompletion.calculate(source,offset);
		
		ICompletionProposal[] result = new ICompletionProposal[words.size()];
		int i=0;
		for (String word: words){
			result[i++] = new SimpleWordProposal(offset, word);
		}
		
		return result;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}
	
	private class SimpleWordProposal implements ICompletionProposal{

		private int offset;
		private String word;

		SimpleWordProposal(int offset, String word){
			this.offset=offset;
			this.word=word;
		}
		
		@Override
		public void apply(IDocument document) {
			try {
				document.replace(offset, 0, word);
			} catch (BadLocationException e) {
				/* ignore*/
			}
			
		}

		@Override
		public Point getSelection(IDocument document) {
			return null;
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

	/* completion listener parts:*/
	
	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
		
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		
	}

}
