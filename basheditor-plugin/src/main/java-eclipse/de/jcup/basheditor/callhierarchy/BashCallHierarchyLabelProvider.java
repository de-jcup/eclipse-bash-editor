package de.jcup.basheditor.callhierarchy;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;

import de.jcup.basheditor.outline.BashEditorOutlineLabelProvider;
import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.script.BashFunction;

public class BashCallHierarchyLabelProvider extends LabelProvider{

    private BashEditorOutlineLabelProvider itemLabelProvider = new BashEditorOutlineLabelProvider();
    
    @Override
    public String getText(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            
            Object entryElement = entry.getElement();
            return renderEntryElement(entryElement)+" - "+renderResource(entry.getResource())+" [line:"+entry.getLine()+",col:"+entry.getColumn()+"] - pos:"+entry.getPos();
            
        }
        return super.getText(element);
    }

    private String renderResource(IResource resource) {
        if (resource ==null) {
            return "null";
        }
        return resource.getName();
    }

    private String renderEntryElement(Object entryElement) {
        if (entryElement==null) {
            return "null";
        }
        if (entryElement instanceof BashFunction) {
            BashFunction function = (BashFunction) entryElement;
            return "function "+function.getName();
        }
        if (entryElement instanceof Item) {
            StyledString styledText = itemLabelProvider.getStyledText(entryElement);
            if (styledText==null) {
                return null;
            }
            return styledText.getString();
        }
        return entryElement.toString();
    }
}
