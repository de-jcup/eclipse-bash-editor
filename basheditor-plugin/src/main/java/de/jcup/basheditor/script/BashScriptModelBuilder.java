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
package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jcup.basheditor.script.parser.ParseToken;
import de.jcup.basheditor.script.parser.TokenParser;
import de.jcup.basheditor.script.parser.TokenParserException;
import de.jcup.basheditor.script.parser.validator.CaseEndsWithEsacValidator;
import de.jcup.basheditor.script.parser.validator.ClosedBlocksValidator;
import de.jcup.basheditor.script.parser.validator.DoEndsWithDoneValidator;
import de.jcup.basheditor.script.parser.validator.IfEndsWithFiValidator;

/**
 * A bash script model builder
 * 
 * @author Albert Tregnaghi
 *
 */
public class BashScriptModelBuilder {
    private boolean ignoreDoValidation;
    private boolean ignoreBlockValidation;
    private boolean ignoreIfValidation;
    private boolean ignoreFunctionValidation;
    private boolean debugMode;
    private boolean ignoreVariables;

    public static class BashScriptModelBuilderConfiguration {
        public boolean fetchVariableUsage;
        public String variableName;
        public boolean ignoreFunctions;
        public String functionUsageName;
        public boolean fetchFunctionUsage;
    }

    /**
     * Parses given script and creates a bash script model
     * 
     * @param bashScript
     * @return a model about bash script
     * @throws BashScriptModelException
     */
    public BashScriptModel build(String bashScript) throws BashScriptModelException {
        return build(bashScript, null);
    }

    /**
     * Parses given script and creates a bash script model
     * 
     * @param bashScript
     * @param configuration
     * @return a model about bash script
     * @throws BashScriptModelException
     */
    public BashScriptModel build(String bashScript, BashScriptModelBuilderConfiguration configuration) throws BashScriptModelException {
        BashScriptModel model = new BashScriptModel();
        if (configuration == null) {
            configuration = new BashScriptModelBuilderConfiguration();// just an empty one as default
        }

        TokenParser parser = new TokenParser();
        List<ParseToken> tokens;
        try {
            tokens = parser.parse(bashScript);
        } catch (TokenParserException e) {
            throw new BashScriptModelException("Was not able to build bashscript", e);
        }
        buildScriptVariablesByTokens(model, false, true, tokens, configuration);

        if (!configuration.ignoreFunctions) {
            buildFunctionsByTokens(model, tokens, configuration);
        }

        List<ValidationResult> results = new ArrayList<>();
        for (BashScriptValidator<List<ParseToken>> validator : createParseTokenValidators()) {
            results.addAll(validator.validate(tokens));
        }

        for (ValidationResult result : results) {
            if (result instanceof BashError) {
                model.errors.add((BashError) result);
            }
        }

        if (debugMode) {
            appendDebugTokens(model, tokens);
        }

        return model;
    }

    private void buildScriptVariablesByTokens(BashVariableRegistry model, boolean acceptLocal, boolean acceptglobal, List<ParseToken> tokens, BashScriptModelBuilderConfiguration configuration) {
        if (ignoreVariables) {
            return;
        }
        Iterator<ParseToken> it = tokens.iterator();
        boolean beforeWaslocal = false;
        while (it.hasNext()) {
            ParseToken token = it.next();

            if (token.isVariableDefinition()) {
                if (beforeWaslocal && !acceptLocal) {
                    continue;
                }
                if (!beforeWaslocal && !acceptglobal) {
                    continue;
                }
                String varName = token.getTextAsVariableName();
                BashVariable var = model.getVariable(varName);

                BashVariableAssignment assignment = new BashVariableAssignment();
                assignment.setStart(token.getStart());
                assignment.setEnd(token.getEnd());
                if (var == null) {
                    var = new BashVariable(varName, assignment);
                    var.setLocal(beforeWaslocal);
                    if (debugMode && it.hasNext()) {
                        ParseToken value = it.next();
                        /* we set this only for debug purpose */
                        var.setInitialValue(value.getText());
                    }
                    model.getVariables().put(varName, var);
                } else {
                    var.getAssignments().add(assignment);
                }

            } else {
                beforeWaslocal = token.isLocalDef();
            }
        }
        inspectBashVariablesIfNecessary(model, tokens, configuration);

    }

    private void inspectBashVariablesIfNecessary(BashVariableRegistry model, List<ParseToken> tokens, BashScriptModelBuilderConfiguration configuration) {
        BashVariable variableToInspect = null;
        if (configuration.fetchVariableUsage && configuration.variableName != null) {
            variableToInspect = model.getVariable(configuration.variableName);
        }

        if (variableToInspect == null) {
            return;
        }

        String normalUsage = "$" + variableToInspect.getName();
        List<VariableUsageVariant> variantsIdentifiableByIndexOf = buildVariableVariants(variableToInspect);

        Iterator<ParseToken> it2 = tokens.iterator();
        while (it2.hasNext()) {
            ParseToken token = it2.next();
            String text = token.getText();

            if (text.equals(normalUsage)) {
                addVariableUsage(variableToInspect, token.getStart(), token.getEnd());
            } else {
                int start;
                int end;

                for (VariableUsageVariant variant : variantsIdentifiableByIndexOf) {
                    int indexOf = -1;

                    do {
                        /* @formatter:off
                         * "${xxx}"  : length:6 
                         * ^-0
                         *       ^-5
                         *        
                         * @formatter:on
                         */
                        if (text.length() < indexOf + 2) {
                            /* avoid index of of bounds ...*/
                            break;
                        }
                        indexOf = text.indexOf(variant.variantFullText, indexOf + 1);
                        if (indexOf != -1) {
                            /* variantFullText found */
                            start = indexOf + token.getStart();
                            end = start + variant.variantFullText.length();

                            start = start +variant.highlightStartAdd;
                            end = end -variant.highlightEndRemove;
                            
                            addVariableUsage(variableToInspect, start, end);
                        }
                    } while (indexOf != -1);
                }
            }
        }
    }

    private void addVariableUsage(BashVariable variableToInspect, int start, int end) {
        BashVariableUsage usage = new BashVariableUsage();
        usage.setStart(start);
        usage.setEnd(end);

        variableToInspect.getUsages().add(usage);
    }

    private class VariableUsageVariant{
        String variantFullText;
        int highlightStartAdd;
        int highlightEndRemove;
        
        private VariableUsageVariant(String variant, int highlightStartAdd, int highlightEndRemove) {
            this.variantFullText=variant;
            this.highlightStartAdd=highlightStartAdd;
            this.highlightEndRemove=highlightEndRemove;
        }
    }
    
    /* builds a list of strings which identifies the bash variable usage */
    private List<VariableUsageVariant> buildVariableVariants(BashVariable variableToInspect) {
        String variableName = variableToInspect.getName();
        // the next line may not be used here directly, because we do not want
        // "xxx" found inside "$xxxy" but only for "$xxx"
        // index of means that the identified part makes it clear, that this is really
        // the variable usage
        // e.g. : "xxx" is represented by "$(xxx)abc"
        String prefix = "$" + variableName;

        List<VariableUsageVariant> variants = new ArrayList<>();
        variants.add(new VariableUsageVariant("$(" + variableName + ")",0,0));
        variants.add(new VariableUsageVariant("${" + variableName + "}",0,0));
        variants.add(new VariableUsageVariant(prefix + "[",0,1));;
        variants.add(new VariableUsageVariant(prefix + " ",0,1));;
        variants.add(new VariableUsageVariant(prefix + "'",0,1));;
        variants.add(new VariableUsageVariant(prefix + "\"",0,1));;
        variants.add(new VariableUsageVariant(prefix + "\\",0,1));;
        
        variants.add(new VariableUsageVariant(prefix + "$",0,1));;
        variants.add(new VariableUsageVariant(prefix + "+",0,1));;
        variants.add(new VariableUsageVariant(prefix + "-",0,1));
        variants.add(new VariableUsageVariant(prefix + "/",0,1));
        
        for (char metaCharacter : BashMetacharacters.METACHARACTERS_WITHOUT_WHITESPACES) {
            variants.add(new VariableUsageVariant(prefix + metaCharacter,0,0));
        }
        return variants;
    }

    private void appendDebugTokens(BashScriptModel model, List<ParseToken> tokens) {
        model.getDebugTokens().addAll(tokens);
    }

    public void setIgnoreBlockValidation(boolean ignoreBlockValidation) {
        this.ignoreBlockValidation = ignoreBlockValidation;
    }

    /**
     * When set to <code>true</code> the builder will not fetch any information
     * about variables!
     * 
     * @param ignoreVariables
     */
    public void setIgnoreVariables(boolean ignoreVariables) {
        this.ignoreVariables = ignoreVariables;
    }

    public void setIgnoreDoValidation(boolean ignoreDoValidation) {
        this.ignoreDoValidation = ignoreDoValidation;
    }

    public void setIgnoreIfValidation(boolean ignoreIfValidation) {
        this.ignoreIfValidation = ignoreIfValidation;
    }

    public void setIgnoreFunctionValidation(boolean ignoreFunctionValidation) {
        this.ignoreFunctionValidation = ignoreFunctionValidation;
    }

    private List<BashScriptValidator<List<ParseToken>>> createParseTokenValidators() {
        List<BashScriptValidator<List<ParseToken>>> validators = new ArrayList<>();
        if (!ignoreDoValidation) {
            validators.add(new DoEndsWithDoneValidator());
        }
        if (!ignoreBlockValidation) {
            validators.add(new ClosedBlocksValidator());
        }
        if (!ignoreIfValidation) {
            validators.add(new IfEndsWithFiValidator());
            validators.add(new CaseEndsWithEsacValidator());
        }
        return validators;
    }

    private class HereDocInspector {
        private String hereDocLiteral;
        private boolean markedAsHereDoc;

        public void inspect(ParseToken token) {
            if (token.isHereDoc()) {
                markAsHereDocStartWhenNotAlreadyHeredoc();
                return;
            }
            /*
             * parser does create heredoc, than literal than other than literal again as
             * tokens
             */
            if (markedAsHereDoc) {
                if (hereDocLiteral == null) {
                    hereDocLiteral = token.getText();
                } else {
                    if (hereDocLiteral.contentEquals(token.getText())) {
                        /* means closing literal found... */
                        reset();
                    } else {
                        /* just content between - do nothing */
                    }
                }
            }
        }

        public boolean isInsideHereDoc() {
            return markedAsHereDoc;
        }

        private void markAsHereDocStartWhenNotAlreadyHeredoc() {
            if (markedAsHereDoc) {
                return;
            }
            markedAsHereDoc = true;
        }

        private void reset() {
            markedAsHereDoc = false;
            hereDocLiteral = null;
        }
    }

    private void buildFunctionsByTokens(BashScriptModel model, List<ParseToken> tokens, BashScriptModelBuilderConfiguration configuration) {
        HereDocInspector his = new HereDocInspector();

        for (int tokenNr = 0; tokenNr < tokens.size(); tokenNr++) {
            ParseToken token = tokens.get(tokenNr);
            his.inspect(token);

            if (his.isInsideHereDoc()) {
                continue;
            }
            FunctionScope functionScope = inspectAndCreateFunctionScope(tokens, tokenNr);

            if (functionScope.isFunction()) {
                String functionName = functionScope.getFunctionName();

                /* ++++++++++++++++++++++++++++++ */
                /* + Scan for curly braces open + */
                /* ++++++++++++++++++++++++++++++ */

                if (!functionScope.hasNextToken()) {
                    if (!ignoreFunctionValidation) {
                        model.errors.add(createBashErrorFunctionMissingCurlyBrace(functionScope.getToken(), functionName));
                    }
                    break;
                }
                ParseToken openCurlyBraceToken = functionScope.nextToken();
                if (!openCurlyBraceToken.isOpenBlock()) {
                    if (!ignoreFunctionValidation) {
                        model.errors.add(createBashErrorFunctionMissingCurlyBrace(functionScope.getToken(), functionName));
                    }
                    continue;
                }
                /* +++++++++++++++++++++++++++++++ */
                /* + Scan for curly braces close + */
                /* +++++++++++++++++++++++++++++++ */

                BashFunction function = createBashFunction(functionScope, functionName);
                if (function.end == -1) {
                    /* no close block found - mark this as an error */
                    if (!ignoreFunctionValidation) {
                        model.errors.add(createBashErrorCloseFunctionCurlyBraceMissing(functionName, openCurlyBraceToken));
                    }
                    break;
                }

                model.functions.add(function);
                /*
                 * function created - last currentTokenNr++ was too much because it will be done
                 * by loop to- so reduce with 1
                 */
                int newTokenNr = functionScope.getCurrentTokenNr() - 1;
                if (newTokenNr > tokenNr) {
                    /* avoid infinite loops... shoud not happen, but... */
                    tokenNr = newTokenNr;
                }

                /* create local variables */
                buildScriptVariablesByTokens(function, true, false, functionScope.getTokensInside(), configuration);

            } else {
                if (functionScope.hasFunctionKeywordPrefix()) {
                    /*
                     * function prefix defined but its not really a function, so something odd!
                     */
                    if (!ignoreFunctionValidation) {

                        model.errors.add(createBashErrorFunctionPrefixFoundButNotAFunction(functionScope.getFunctionName(), functionScope.getToken()));
                    }
                }
            }
        }

        inspectFunctionAndusageIfNecessary(model, tokens, configuration);
    }

    private void inspectFunctionAndusageIfNecessary(BashScriptModel model, List<ParseToken> tokens, BashScriptModelBuilderConfiguration configuration) {
        if (!configuration.fetchFunctionUsage || configuration.functionUsageName == null) {
            return;
        }
        BashFunction functionToInspect = model.findBashFunctionByName(configuration.functionUsageName);
        if (functionToInspect == null) {
            return;
        }
        for (ParseToken token : tokens) {
            String tokenText = token.getText();
            if (configuration.functionUsageName.equals(tokenText)) {
                if (token.getStart() == functionToInspect.getPosition()) {
                    /* function itself - so ignore */
                    continue;
                }
                BashFunctionUsage usage = new BashFunctionUsage();
                usage.setStart(token.getStart());
                usage.setEnd(token.getEnd());

                functionToInspect.getUsages().add(usage);
            }
        }
    }

    protected FunctionScope inspectAndCreateFunctionScope(List<ParseToken> t, int tokenNr) {
        FunctionScope functionScope = new FunctionScope(t);

        functionScope.setCurrentTokenNr(tokenNr);
        functionScope.setToken(functionScope.nextToken());

        /* ++++++++++++++++++++++ */
        /* + Scan for functions + */
        /* ++++++++++++++++++++++ */
        if (functionScope.hasFunctionKeywordPrefix() && functionScope.hasNextToken()) {
            functionScope.setFunctionStart(functionScope.getToken().getStart());
            functionScope.setToken(functionScope.nextToken());
        }

        inspectPotentialFunctionUntilEndingBracketOrNextIsOpenCurly(functionScope);
        return functionScope;
    }

    protected BashFunction createBashFunction(FunctionScope functionScope, String functionName) {
        BashFunction function = new BashFunction();
        function.lengthToNameEnd = functionScope.getFunctionEnd() - functionScope.getFunctionStart();
        function.position = functionScope.getFunctionStart();
        function.name = functionName;
        function.end = -1;
        if (Boolean.TRUE.equals(functionScope.hasFunctionKeywordPrefix())){
            function.positionFunctionName=function.position+"function ".length();
        }else {
            function.positionFunctionName=function.position;
        }

        scanForFunctionEnd(functionScope, function);

        return function;
    }

    protected void scanForFunctionEnd(FunctionScope functionScope, BashFunction function) {
        while (functionScope.hasNextToken()) {
            ParseToken nextToken = functionScope.nextToken();
            functionScope.getTokensInside().add(nextToken);
            if (nextToken.isCloseBlock()) {
                function.end = nextToken.getEnd();
                break;
            }
        }
    }

    protected void inspectPotentialFunctionUntilEndingBracketOrNextIsOpenCurly(FunctionScope functionScope) {
        /*
         * either we come from "function xyz()" or just "xyz()" -> current token is
         * always methodname
         */
        if (!functionScope.hasNextToken()) {
            return;
        }
        if (!functionScope.isTokenHavingLegalFunctionName()) {
            return;
        }
        ParseToken followToken = functionScope.nextToken(); // either () or just
                                                            // brackets are used
        boolean tokenWithEndingBrackets = followToken.hasLength(2) && followToken.endsWithFunctionBrackets();
        if (tokenWithEndingBrackets) {
            functionScope.setIsFunction(true);
            return;
        }
        if (followToken.isOpenBlock()) {
            /*
             * only allowed for "function xyz {}" but not for "xyz {}" - means without
             * keyword function there must be brackets used!
             */
            functionScope.setIsFunction(functionScope.hasFunctionKeywordPrefix());
            functionScope.backToken();// move back to have access to curly
                                      // bracket from outside as next token,
                                      // necessary for validation when function
            return;
        }
        /* could be 'MethodName ( )' <!-- */
        if (!followToken.isFunctionStartBracket()) {
            return;
        }

        if (!functionScope.hasNextToken()) {
            return;
        }

        followToken = functionScope.nextToken();
        functionScope.setIsFunction(followToken.isFunctionEndBracket());
    }

    private BashError createBashErrorFunctionPrefixFoundButNotAFunction(String functionName, ParseToken token) {
        return new BashError(token.getStart(), token.getEnd(), "Keyword function used but it's not a valid function definition: '" + functionName + "'.");
    }

    private BashError createBashErrorCloseFunctionCurlyBraceMissing(String functionName, ParseToken openCurlyBraceToken) {
        return new BashError(openCurlyBraceToken.getStart(), openCurlyBraceToken.getEnd(), "This curly brace is not closed. So function '" + functionName + "' is not valid.");
    }

    private BashError createBashErrorFunctionMissingCurlyBrace(ParseToken token, String functionName) {
        return new BashError(token.getStart(), token.getEnd(), "The function '" + functionName + "' is not valid because no opening curly brace found.");
    }

    public void setDebug(boolean debugMode) {
        this.debugMode = debugMode;
    }

}
