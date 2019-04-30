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
package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.script.parser.ParseToken;

class FunctionScope {
    private Boolean hasFunctionKeyWordBefore;
    private int currentTokenNr;
    private List<ParseToken> tokens;
    private boolean isFunction;
    private Integer functionStart;
    private ParseToken token;
    private List<ParseToken> tokensInside=new ArrayList<ParseToken>();
    
    public FunctionScope(List<ParseToken> tokens) {
        this.tokens = tokens;
    }
    public List<ParseToken> getTokensInside() {
        return tokensInside;
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