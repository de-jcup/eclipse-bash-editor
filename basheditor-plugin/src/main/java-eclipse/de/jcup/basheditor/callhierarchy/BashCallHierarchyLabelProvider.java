package de.jcup.basheditor.callhierarchy;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.EclipseDeveloperSettings;
import de.jcup.basheditor.EclipseUtil;
import de.jcup.basheditor.outline.BashEditorOutlineLabelProvider;
import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.script.BashFunction;

public class BashCallHierarchyLabelProvider extends LabelProvider{
    private static final Image IMAGE_DEFAULT_ENTRY = EclipseUtil.getImage("icons/view/call_hierarchy_entry_default.png", BashEditorActivator.getDefault().getPluginID());
    private BashEditorOutlineLabelProvider itemLabelProvider = new BashEditorOutlineLabelProvider();
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            return getImageFor((BashCallHierarchyEntry)element);
        }
        return super.getImage(element);
    }
    
    private Image getImageFor(BashCallHierarchyEntry element) {
        
        return IMAGE_DEFAULT_ENTRY;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof BashCallHierarchyEntry) {
            BashCallHierarchyEntry entry = (BashCallHierarchyEntry) element;
            
            Object entryElement = entry.getElement();
            StringBuilder sb = new StringBuilder();
            sb.append(renderEntryElement(entryElement));
            sb.append(": ");
            sb.append(renderResourceName(entry.getResource()));
            if (entry.getLine()>0) {
                sb.append(", line:").append(entry.getLine());
                sb.append(", column:").append(entry.getColumn());
            }
            if (EclipseDeveloperSettings.SHOW_CALLHIERARCHY_TRACEMODE) {
                sb.append(", offset:").append(entry.getOffset());
            }
            sb.append(", location:");
            sb.append(renderResourceLocation(entry.getResource()));
            return sb.toString();
            
        }
        return super.getText(element);
    }


    private String renderEntryElement(Object entryElement) {
        if (entryElement==null) {
            return "[UNKOWN]";
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
        if (entryElement instanceof IResource) {
            return "script";
        }
        return entryElement.toString();
    }
    private String renderResourceName(IResource resource) {
        if (resource ==null) {
            return "null";
        }
        return resource.getName();
    }
    private String renderResourceLocation(IResource resource) {
        if (resource ==null) {
            return "null";
        }
        IPath location = resource.getLocation();
        if (location==null) {
            return "no location";
        }
        return location.toString();
    }
}
