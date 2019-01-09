package de.jcup.basheditor.script;

import java.util.List;

import de.jcup.basheditor.script.parser.ParseToken;

class FunctionScope {
    private Boolean hasFunctionKeyWordBefore;
    private int currentTokenNr;
    private List<ParseToken> tokens;
    private boolean isFunction;
    private Integer functionStart;
    private ParseToken token;

    public FunctionScope(List<ParseToken> tokens) {
        this.tokens = tokens;
    }

    public void setToken(ParseToken token) {
        this.token = token;
    }

    public ParseToken getToken() {
        return token;
    }

    public void setCurrentTokenNr(int tokenNr) {
        currentTokenNr = tokenNr;
    }

    public int tokenCount() {
        return tokens.size();
    }

    public int getCurrentTokenNr() {
        return currentTokenNr;
    }

    public ParseToken nextToken() {
        return tokens.get(currentTokenNr++);
    }

    public void backToken() {
        currentTokenNr--;
    }

    public boolean hasNextToken() {
        return hasPos(currentTokenNr, tokens);
    }

    private boolean hasPos(int pos, List<?> elements) {
        if (elements == null) {
            return false;
        }
        return pos < elements.size();
    }

    public int getFunctionEnd() {
        return token.getEnd();
    }

    public void setIsFunction(boolean isFunction) {
        this.isFunction = isFunction;
    }

    public boolean isFunction() {
        if (!isFunction) {
            isFunction = token.isFunction();
        }
        return isFunction;
    }

    public void setFunctionStart(Integer functionStart) {
        this.functionStart = functionStart;
    }

    public int getFunctionStart() {
        if (functionStart == null) {
            functionStart = Integer.valueOf(token.getStart());
        }
        return functionStart.intValue();
    }

    public String getFunctionName() {
        return token.getTextAsFunctionName();
    }

    public boolean isTokenHavingLegalFunctionName() {
        return token.isLegalFunctionName();
    }

    public boolean hasFunctionKeywordPrefix() {
        if (hasFunctionKeyWordBefore == null) {
            hasFunctionKeyWordBefore = token.isFunctionKeyword();
        }
        return hasFunctionKeyWordBefore.booleanValue();
    }

}