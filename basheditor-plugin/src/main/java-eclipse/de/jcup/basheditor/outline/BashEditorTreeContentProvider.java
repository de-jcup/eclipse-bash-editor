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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.basheditor.scriptmodel.BashFunction;
import de.jcup.basheditor.scriptmodel.BashScriptModel;
import de.jcup.basheditor.scriptmodel.BashScriptModelBuilder;

public class BashEditorTreeContentProvider implements ITreeContentProvider {

	private Item[] items;
	private BashScriptModelBuilder modelBuilder;

	public BashEditorTreeContentProvider() {
		this.modelBuilder = new BashScriptModelBuilder();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IDocument) {
			IDocument document = (IDocument) inputElement;
			String text = document.get();
			
			BashScriptModel model = modelBuilder.build(text);
			this.items = build(model);
		}
		if (items!=null && items.length>0){
			return items;
		}
		return new Object[] { "This bash script does not contain any functions" };
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

	private Item[] build(BashScriptModel model) {
		List<Item> list = new ArrayList<>();
		for (BashFunction function : model.getFunctions()) {
			Item item = new Item();
			item.name = function.getName();
			item.type = ItemType.FUNCTION;
			item.offset = function.getPosition();
			item.length=function.getLengthToNameEnd();
			list.add(item);
		}
		return list.toArray(new Item[list.size()]);
	}

}
