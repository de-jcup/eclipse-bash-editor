package de.jcup.basheditor.scriptmodel;

import java.util.List;

public class ClosedBlocksValidator extends AbstractParseTokenListValidator {

	@Override
	protected void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {

		int amountOfOpened = 0;
		int amountOfClosed = 0;

		ParseToken lastWorkingOpen = null;
		ParseToken lastWorksBeforeClose = null;
		for (ParseToken token : tokens) {
			if (token.isOpenBlock()) {
				if (amountOfClosed == amountOfOpened) {
					lastWorkingOpen = token;
				}
				amountOfOpened++;
			} else if (token.isCloseBlock()) {
				if (amountOfClosed == amountOfOpened) {
					lastWorksBeforeClose = token;
				}
				amountOfClosed++;
			}
		}
		if (amountOfClosed==amountOfOpened){
			return;
		}
		if (amountOfClosed > amountOfOpened){
			if (lastWorksBeforeClose == null) {
				result.add(new BashError(0, 0, "More closing brackets than opened ones. It seems you got a { missing"));
			}else{
				result.add(new BashError(lastWorksBeforeClose.start, lastWorksBeforeClose.end, "It seems this closing bracket is missing a opening one"));
			}
		}else{
			if (lastWorkingOpen == null) {
				result.add(new BashError(0, 0, "More opening brackets than closed ones. It seems you got a } missing"));
			}else{
				result.add(new BashError(lastWorkingOpen.start, lastWorkingOpen.end, "It seems this opening bracket is missing a closing one."));
			}
		}
		
	}

}
