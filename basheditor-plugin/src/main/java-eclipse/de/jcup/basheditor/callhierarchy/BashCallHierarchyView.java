package de.jcup.basheditor.callhierarchy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashCallHierarchyView extends ViewPart {
    private static final ImageDescriptor IMG_DESC_SEARCH_IN_WORKSPACE = EclipseUtil.createImageDescriptor("icons/view/generic_elements.png", BashEditorActivator.PLUGIN_ID);
    private static final ImageDescriptor IMG_DESC_SEARCH_IN_SAME_PROJECT = EclipseUtil.createImageDescriptor("icons/view/generic_element.png", BashEditorActivator.PLUGIN_ID);

    public static final String VIEW_ID = "de.jcup.basheditor.callhierarchy.BashCallHierarchyView";
    private TreeViewer viewer;
    private BashCallHierarchyContentProvider contentProvider;
    private boolean projectOnly = true;

    @Override
    public void createPartControl(Composite parent) {
        contentProvider = new BashCallHierarchyContentProvider();

        viewer = new TreeViewer(parent);
        viewer.setContentProvider(contentProvider);
        BashCallHierarchyLabelProvider labelAndDecorationProvider = new BashCallHierarchyLabelProvider();
        viewer.setLabelProvider(new DecoratingLabelProvider(labelAndDecorationProvider,PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

        viewer.addDoubleClickListener(event -> {
            ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection ss = (IStructuredSelection) selection;
                Object element = ss.getFirstElement();
                handleDoubleClickOnElement(element);
            }
        });
        viewer.setAutoExpandLevel(2);

        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.add(new ProjectOnlyFilterAction());

    }

    private class ProjectOnlyFilterAction extends Action {

        ProjectOnlyFilterAction() {
            initImage();
            initText();
        }

        @Override
        public void run() {
            projectOnly = !projectOnly;

            initText();
            initImage();

            // clean old data
            BashCallHierarchyView.showCallHierarchy(null);
        }

        private void initImage() {
            setImageDescriptor(projectOnly ? IMG_DESC_SEARCH_IN_SAME_PROJECT : IMG_DESC_SEARCH_IN_WORKSPACE);
        }

        private void initText() {
            setText(projectOnly ? "Searches only in project" : "Searches complete workspace");
            setDescription(getText()+"  - " + (projectOnly ? "Click to switch to search in all workspace projects" : "Click to search only in same project"));
        }
    }

    public static final void showCallHierarchy(BashCallHierarchyEntry entry) {
        try {
            IWorkbenchPage page = EclipseUtil.getActivePage();
            if (page == null) {
                return;
            }
            IViewPart viewpart = page.showView(BashCallHierarchyView.VIEW_ID);
            if (viewpart instanceof BashCallHierarchyView) {
                BashCallHierarchyView bchv = (BashCallHierarchyView) viewpart;
                if (entry == null) {
                    bchv.setRootElement(null);
                } else {
                    bchv.setRootElement(new BashCallHierarchyRootElement(entry));
                }
                return;
            }
        } catch (PartInitException e) {
            BashEditorUtil.logError("Not able to show hierarchy view", e);
        }
    }

    private void handleDoubleClickOnElement(Object element) {
        if (!(element instanceof BashCallHierarchyEntry)) {
            return;
        }
        BashCallHierarchyEntry be = (BashCallHierarchyEntry) element;
        IResource resource = be.getResource();
        if (!(resource instanceof IFile)) {
            return;
        }
        IFile file = (IFile) resource;
        int offset = be.getOffset();

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            IEditorPart editor = IDE.openEditor(page, file);
            if (editor instanceof AbstractTextEditor) {
                AbstractTextEditor ate = (AbstractTextEditor) editor;
                ate.selectAndReveal(offset + be.getColumn(), be.getLength());
            }
        } catch (PartInitException e) {
            BashEditorUtil.logError("Was not able to open file:" + file.getName(), e);
        }

    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void setRootElement(BashCallHierarchyRootElement root) {
        if (root == null || root.getEntries() == null || root.getEntries().length == 0) {
            viewer.setInput(null);
            return;
        }
        contentProvider.setProjectScope(null);
        
        if (projectOnly) {
            IProject projectScope = null;
            BashCallHierarchyEntry[] entries = root.getEntries();
            for (BashCallHierarchyEntry entry : entries) {
                IResource resource = entry.getResource();
                if (resource != null) {
                    projectScope = resource.getProject();
                    if (projectScope != null) {
                        break;
                    }
                }
            }
            contentProvider.setProjectScope(projectScope);
        }
        viewer.setInput(root);

    }

}
