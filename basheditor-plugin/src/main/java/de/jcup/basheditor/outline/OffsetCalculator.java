/*
 * Copyright 2016 Albert Tregnaghi
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

public class OffsetCalculator {

	private static final int UNKNOWN_OFFSET = -1;
	
	/**
	 * Calculates offset for given line and column in lines
	 * @param lines
	 * @param line
	 * @param column
	 * @return offset calculated or -1 when offset is unknown
	 */
	public int calculatetOffset(CharSequence[] lines, int line, int column) {
		if (lines==null){
			return UNKNOWN_OFFSET;
		}
		if (line > lines.length){
			return UNKNOWN_OFFSET;
		}
		int lineIndex = line-1;
		CharSequence lastSequence = lines[lineIndex];
		if (column>lastSequence.length()){
			return UNKNOWN_OFFSET;
		}
		
		int columnIndex= column-1;
		int offset = 0;
		for (int index=0;index<lines.length;index++){
			CharSequence sequence = lines[index]; 
			if (index==lineIndex){
				// ABC
				// D
				// ^---Offset of D ist not column(1) but former offset + column-1(0)
				offset+=columnIndex;
				break;
			}else{
				offset+=sequence.length();
			}
		}
		
		return offset;
	}

}
