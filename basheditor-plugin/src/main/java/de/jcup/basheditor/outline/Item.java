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

public class Item {

	ItemType type;
	String name;
	int offset;
	int length;
	int endOffset;
	List<Item> children;
	
	/**
	 * @return item type , or <code>null</code>
	 */
	public ItemType getItemType(){
		return type;
	}
	
	public boolean hasChildren() {
	    return children!=null && children.size()>0;
	}
	
	/**
	 * Gets children list. List will be created lazily.
	 * To prevent unused objects, use {@link #hasChildren()} before
	 * @return list, never <code>null</code>
	 */
	public List<Item> getChildren(){
	    if (children==null) {
	        children=new ArrayList<Item>();
	    }
	    return children;
	}

	public String getName() {
		return name;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
	
	public int getEndOffset() {
		return endOffset;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Item:");
		sb.append("name:");
		sb.append(name);
		sb.append(",type:");
		sb.append(type);
		sb.append(",offset:");
		sb.append(offset);
		sb.append(",length:");
		sb.append(length);
		sb.append(",endOffset:");
		sb.append(endOffset);
		return sb.toString();
	}

	public String buildSearchString() {
		return name;
	}
}
