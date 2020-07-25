package de.jcup.basheditor.callhierarchy;

import java.util.List;

import de.jcup.basheditor.workspacemodel.SharedBashModel;
import de.jcup.basheditor.workspacemodel.SharedModelMethodTarget;

public class BashCallHierarchyModelBuilder {

    public BashCallHierarchyModel createBashCallHierarchy(SharedBashModel sharedModel, String selection) {
        BashCallHierarchyModel model = new BashCallHierarchyModel();
      
        List<SharedModelMethodTarget> targets = sharedModel.findResourcesHavingMethods(selection, null);
        if (! targets.isEmpty()) {
            /* means this is method call */
            BashCallHierarchyEntry entry = new BashCallHierarchyEntry();
            model.getRootElements().add(entry);
            
            entry.name=selection;
            entry.type=BashCallType.METHOD;
            
            for (SharedModelMethodTarget target: targets) {
                BashCallHierarchyEntry child = new BashCallHierarchyEntry();
                child.name=target.getFunction().getName();
                child.resource=target.getResource();
                entry.children.add(child);
                child.parent=entry;
            }
        }
        /* FIXME de-jcup, 2020-23-06: change this . remove the mode, implement lazy tree, every expanding of a node should 
         * investigate the model again and create subnodes - so not fetching all at first time but lazy!*/
        return model;
    }
}
