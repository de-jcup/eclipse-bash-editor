/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.basheditor.debug;

import java.util.ArrayList;


public class BashNetworkVariableData implements Comparable<BashNetworkVariableData> {
	private String name;
	private String lowerCasedName;
	private String value;
	private ArrayList<String> arrayList;

	public BashNetworkVariableData(String variableName) {
		if (variableName==null) {
			this.name="null";
		}else {
			this.name=variableName;
		}
		this.lowerCasedName=name.toLowerCase();
	}

	public String getStringValue(int index) {
		if (index >= arrayList.size()) {
			return "";
		}
		String s = arrayList.get(index);
		return s;
	}

	public int compareTo(BashNetworkVariableData o) {
		return lowerCasedName.compareTo(o.name.toLowerCase());
	}

	public int getIntValue(int index) {
		try{
			String stringValue = getStringValue(index);
			return Integer.parseInt(stringValue);
		}catch(NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}

	@Override
	public String toString() {
		return "BashNetworkVariableData [name=" + name + ", value=" + value + ", array=" + arrayList + "]";
	}

	public String getName() {
		return name;
	}

	public String getStringValue() {
		return value;
	}

	public boolean isArray() {
		return arrayList != null;
	}

	public int getArraySize() {
		if (arrayList==null) {
			return 0;
		}
		return arrayList.size();
	}

	public void removeFromArray(int index) {
		if (arrayList==null) {
			return;
		}
		arrayList.remove(index);
	}

	public void setValue(String value) {
		this.value=value;
	}

	public void defineAsArray() {
		arrayList = new ArrayList<>();
	}

	public void addArrayValue(String value) {
		if (!isArray()) {
			defineAsArray();
		}
		arrayList.add(value);
	}
}