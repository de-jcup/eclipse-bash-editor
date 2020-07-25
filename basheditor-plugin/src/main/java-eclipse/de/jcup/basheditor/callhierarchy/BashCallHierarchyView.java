package de.jcup.basheditor.callhierarchy;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.workspacemodel.SharedBashModel;

public class BashCallHierarchyView extends ViewPart{

    public static final String VIEW_ID = "de.jcup.basheditor.callhierarchy.BashCallHierarchyView";
    private TreeViewer viewer;
    private BashCallHierarchyContentProvider contentProvider;
    
    @Override
    public void createPartControl(Composite parent) {
        contentProvider = new BashCallHierarchyContentProvider();
        
        viewer = new TreeViewer(parent);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new BashCallHierarchyLabelProvider());
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void showTextSelectionHierarchy(String text, int offset) {
        SharedBashModel sharedModel = BashEditorActivator.getDefault().getModel();
        BashCallHierarchyModelBuilder builder = new BashCallHierarchyModelBuilder();
        BashCallHierarchyModel model = builder.createBashCallHierarchy(sharedModel, text);
        
        viewer.setInput(model);
        
    }

}
