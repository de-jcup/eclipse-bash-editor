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
package de.jcup.basheditor.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.basheditor.scriptmodel.BashFunction;
import de.jcup.basheditor.scriptmodel.BashScriptModel;

public class BashEditorTreeContentProvider implements ITreeContentProvider {

	private static final String BASH_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS = "Bash script does not contain any functions";
	private static final Object[] RESULT_WHEN_EMPTY = new Object[] { BASH_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS };
	private Object[] items;
	
	BashEditorTreeContentProvider(){
		items = RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (! (inputElement instanceof BashScriptModel)){
			return new Object[] { "Unsupported input element" };
		}
		if (items != null && items.length > 0) {
			return items;
		}
		return RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

	private Object[] createItems(BashScriptModel model) {
		List<Object> list = new ArrayList<>();
		for (BashFunction function : model.getFunctions()) {
			Item item = new Item();
			item.name = function.getName();
			item.type = ItemType.FUNCTION;
			item.offset = function.getPosition();
			item.length = function.getLengthToNameEnd();
			list.add(item);
		}
		if (list.isEmpty()){
			list.add(BASH_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS);
		}
		if (model.hasErrors()){
			list.add(0,"Bash script contains errors.");
		}
		return list.toArray(new Object[list.size()]);
	}

	public void rebuildTree(BashScriptModel model) {
		if (model == null) {
			items = null;
			return;
		}
		items = createItems(model);
	}

}
