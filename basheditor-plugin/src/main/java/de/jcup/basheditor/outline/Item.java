package de.jcup.basheditor.outline;

public class Item {

	ItemType type;
	String name;
	int offset;
	int length;
	
	/**
	 * @return item type , or <code>null</code>
	 */
	public ItemType getItemType(){
		return type;
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
}
