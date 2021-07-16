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
