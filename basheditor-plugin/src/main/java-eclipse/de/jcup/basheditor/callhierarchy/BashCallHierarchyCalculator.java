package de.jcup.basheditor.callhierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.jcup.basheditor.outline.Item;
import de.jcup.basheditor.outline.ItemType;
import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.workspacemodel.SharedBashModel;

public class BashCallHierarchyCalculator {
    
    public BashCallHierarchyCalculator() {
    }

    public List<BashCallHierarchyEntry> findCallers(BashCallHierarchyEntry parent, SharedBashModel model, Item item, IProject projectScope){
        if (model == null || item==null) {
            return Collections.emptyList();
        }
    
        ItemType itemType = item.getItemType();
        if (itemType==null || itemType!=ItemType.FUNCTION) {
            return Collections.emptyList();
        }
        
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
//        List<SharedModelMethodTarget> targets = model.findResourcesHavingMethods(item.getName(), null);
//        
//        for (SharedModelMethodTarget target: targets) {
//            BashCallHierarchyEntry child = new BashCallHierarchyEntry(parent,model,this);
//            child.setElement(target);
//            entries.add(child);
//        }
        entries.addAll(model.findResourcesContainingText(parent, item.buildSearchString(),projectScope));
        return entries;
        
    }
    
    public List<BashCallHierarchyEntry> findCallers(BashCallHierarchyEntry parent, SharedBashModel model, BashFunction function, IProject projectScope){
        if (model == null || function==null) {
            return Collections.emptyList();
        }
    
        
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        entries.addAll(model.findResourcesContainingText(parent, function.getName(),projectScope));
        return entries;
        
    }
    
    public List<BashCallHierarchyEntry> findChildren(SharedBashModel model, BashCallHierarchyEntry entry, IProject projectScope){
        if (model == null || entry==null || entry.getElement()==null) {
            return Collections.emptyList();
        }
        Object element = entry.getElement();
        /* when item */
        if (element instanceof Item) {
            return findCallers(entry, model, (Item)element,projectScope);
        }
        if (element instanceof BashFunction) {
            return findCallers(entry, model, (BashFunction)element,projectScope);
        }
        if (element instanceof IResource) {
            return findCallersAndIncludes(entry,model,(IResource)element,projectScope);
        }
        /* when other */
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        
        
        return entries;
    }

    private List<BashCallHierarchyEntry> findCallersAndIncludes(BashCallHierarchyEntry parent, SharedBashModel model, IResource element, IProject projectScope) {
        List<BashCallHierarchyEntry> entries = new ArrayList<>();
        entries.addAll(model.findResourcesContainingText(parent, element.getName(),projectScope));
        return entries;
    }
}
