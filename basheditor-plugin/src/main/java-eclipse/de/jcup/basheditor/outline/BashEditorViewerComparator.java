/*
 * Copyright 2020 Albert Tregnaghi
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

import org.eclipse.jface.viewers.ViewerComparator;

public class BashEditorViewerComparator extends ViewerComparator {

    private int CATEGORY_OTHER=1;
    private int CATEGORY_FUNCTIONS=10;

    @Override
    public int category(Object element) {
        if (!(element instanceof Item)) {
            return super.category(element);
        }
        Item item = (Item) element;
        ItemType type = item.getItemType();
        if (type == null) {
            return super.category(element);
        }
        
        if (type == ItemType.FUNCTION) {
            return CATEGORY_FUNCTIONS;
        }
        return CATEGORY_OTHER;
    }
}
