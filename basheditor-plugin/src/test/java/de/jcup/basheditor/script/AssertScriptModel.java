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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

public class AssertScriptModel {

	public static AssertScriptModel assertThat(BashScriptModel model) {
		if (model == null) {
			throw new IllegalArgumentException("model is null");
		}
		return new AssertScriptModel(model);
	}

	private BashScriptModel model;

	private AssertScriptModel(BashScriptModel model) {
		this.model = model;
	}

	public AssertScriptModel hasNoFunctions() {
		return hasFunctions(0);
	}
	
	public AssertScriptModel and() {
	    /* just syntax sugar ...*/
	    return this;
	}
		
	public AssertScriptModel hasFunctions(int amount) {
		Collection<BashFunction> functions = getFunctions();
		if (amount!=functions.size()){
			assertEquals("bash script model has not expected amount of functions \nfunctions found:"+functions,amount, functions.size());
		}
		return this;
	}

	public AssertScriptModel hasNoFunction(String functionName) {
		hasFunction(functionName, false, -1);
		return this;
	}

	public AssertFuction hasFunction(String functionName) {
		return hasFunction(functionName, true, -1);
	}

	public AssertFuction hasFunctionWithPosition(String functionName, int expectedPosition) {
		return hasFunction(functionName, true, expectedPosition);

	}
	
	public class AssertFuction{
	    private BashFunction function;

        public AssertFuction(BashFunction function) {
	        this.function=function;
	    }

        AssertScriptModel and() {
	        return AssertScriptModel.this;
	    }
        
        public AssertFuction hasNoVariables() {
            if (function.getVariables().isEmpty()){
                return this;
            }
            fail("Model has variables:"+model.getVariables());
            return this;
        }
        
        public AssertVariable hasVariable(String varName) {
            BashVariable variable = function.getVariable(varName);
            assertNotNull("Variable not found:"+varName, variable);
            return new AssertVariable(variable);
        }
	}

	private AssertFuction hasFunction(String functionName, boolean excpectedFunctionExists, int expectedPosition) {
		BashFunction found = null;

		for (BashFunction function : getFunctions()) {
			if (function.getName().equals(functionName)) {
				found = function;
			}
			if (found != null) {
				break;
			}
		}
		/* assert function available or not */
		if (found != null) {
			if (!excpectedFunctionExists) {
				fail("Did not expect, but script has function with name:" + functionName);
			}

			/* assert start if wanted */
			assertFunctionHasPosition(found, expectedPosition);

		} else {
			if (excpectedFunctionExists) {
				fail("This script has NO function with name:" + functionName+". But it contains following functions:"+createFunctionStringList());
			}
		}

		return new AssertFuction(found);
	}

	private StringBuilder createFunctionStringList() {
		StringBuilder sb = new StringBuilder();
		for (BashFunction function : getFunctions()){
			sb.append('\'');
			sb.append(function.name);
			sb.append('\'');
			sb.append(',');
		}
		if (sb.length()==0){
		    sb.append("-- no functions at all!");
		}
		return sb;
	}

	private void assertFunctionHasPosition(BashFunction found, int expectedPosition) {
		if (found == null) {
			throw new IllegalArgumentException("wrong usage of this method, found may not be null here!");
		}
		if (expectedPosition == -1) {
			return;
		}
		assertEquals("Position of function is not as expected!", expectedPosition, found.position);

	}

	private Collection<BashFunction> getFunctions() {
		Collection<BashFunction> functions = model.getFunctions();
		assertNotNull(functions);
		return functions;
	}
	
	private Collection<BashError> getErrors() {
		Collection<BashError> errors = model.getErrors();
		assertNotNull(errors);
		return errors;
	}

	public AssertScriptModel hasErrors(int expectedAmountOfErrors) {
		assertEquals("Script has not expected amount of errors!",expectedAmountOfErrors, getErrors().size());
		return this;
	}

	public AssertScriptModel hasNoErrors() {
		return hasErrors(0);
	}

	public AssertScriptModel hasNoDebugTokens() {
		assertFalse(model.hasDebugTokens());
		return this;
	}

	public AssertScriptModel hasDebugTokens(int amount) {
		assertTrue(model.hasDebugTokens());
		assertEquals("Amount of debug tokens not as expected", amount ,model.getDebugTokens().size());
		return this;
	}
	
	public AssertScriptModel hasNoVariables() {
	    if (model.getVariables().isEmpty()){
	        return this;
	    }
	    fail("Model has variables:"+model.getVariables());
	    return this;
	}
	
	public AssertVariable hasVariable(String varName) {
        BashVariable variable = model.getVariable(varName);
        assertNotNull("Variable not found:"+varName, variable);
        return new AssertVariable(variable);
    }

}
