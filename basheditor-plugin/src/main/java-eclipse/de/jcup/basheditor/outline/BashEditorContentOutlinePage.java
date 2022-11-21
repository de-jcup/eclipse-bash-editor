/*
 * Copyright 2017 Albert Tregnaghi
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.callhierarchy.BashCallHierarchyEntry;
import de.jcup.basheditor.callhierarchy.BashCallHierarchyView;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {
    private static final ImageDescriptor IMG_DESC_OPEN_CALL_HIERARCHY = EclipseUtil.createImageDescriptor("icons/view/call_hierarchy.png", BashEditorActivator.PLUGIN_ID);
    private static final ImageDescriptor IMG_DESC_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/synced.png", BashEditorActivator.PLUGIN_ID);
    private static final ImageDescriptor IMG_DESC_NOT_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/sync_broken.png", BashEditorActivator.PLUGIN_ID);
    private static final ImageDescriptor IMG_DESC_ALPHABETICAL_SORT= EclipseUtil.createImageDescriptor("/icons/outline/alphab_sort_co.png", BashEditorActivator.PLUGIN_ID);

    private BashEditorTreeContentProvider contentProvider;
    private Object input;
    private BashEditor editor;
    private BashEditorOutlineLabelProvider labelProvider;

    private boolean linkingWithEditorEnabled;
    private boolean ignoreNextSelectionEvents;
    private ToggleLinkingAction toggleLinkingAction;
    private TreeViewer viewer;
    public boolean sortOutlineAlphabeticalEnabled;
    private ToggleAlphabeticalSortAction switchSortAlphabeticalAction;

    public BashEditorContentOutlinePage(BashEditor editor) {
        this.editor = editor;
        this.contentProvider = new BashEditorTreeContentProvider();
    }

    public BashEditorTreeContentProvider getContentProvider() {
        return contentProvider;
    }

    public void createControl(Composite parent) {
        super.createControl(parent);

        labelProvider = new BashEditorOutlineLabelProvider();

        viewer = getTreeViewer();
        viewer.setContentProvider(contentProvider);
        viewer.addDoubleClickListener(this);
        viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
        viewer.addSelectionChangedListener(this);

        createContextMenu(viewer);

        /* it can happen that input is already updated before control created */
        if (input != null) {
            viewer.setInput(input);
        }
        toggleLinkingAction = new ToggleLinkingAction();
        toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);

        switchSortAlphabeticalAction =new ToggleAlphabeticalSortAction();
        
        IActionBars actionBars = getSite().getActionBars();

        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.add(toggleLinkingAction);
        toolBarManager.add(switchSortAlphabeticalAction);

        IMenuManager viewMenuManager = actionBars.getMenuManager();
        viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$

        viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
        viewMenuManager.add(toggleLinkingAction);

        /*
         * when no input is set on init state - let the editor rebuild outline (async)
         */
        if (input == null && editor != null) {
            editor.rebuildOutline();
        }

    }

    /**
     * Creates the context menu
     *
     * @param viewer
     */
    protected void createContextMenu(Viewer viewer) {
        MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });

        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    /**
     * Fill dynamic context menu
     *
     * @param contextMenu
     */
    protected void fillContextMenu(IMenuManager contextMenu) {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        Object element = selection.getFirstElement();

        if (!(element instanceof Item)) {
            return;
        }
        Item item = (Item) element;
        contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        if (item.getItemType() == ItemType.FUNCTION) {
            contextMenu.add(new OpenCallHierarchyAction(item));
        }
    }

    private class OpenCallHierarchyAction extends Action {
        private Item item;

        public OpenCallHierarchyAction(Item item) {
            this.item = item;
            setText("Open Bash Call Hierarchy");
            setImageDescriptor(IMG_DESC_OPEN_CALL_HIERARCHY);
        }

        @Override
        public void run() {
            BashCallHierarchyEntry entry = new BashCallHierarchyEntry();
            entry.setElement(item);
            entry.setResource(editor.resolveResource());
            entry.setOffset(item.getOffset());
            entry.setLength(item.getLength());

            BashCallHierarchyView.showCallHierarchy(entry);
        }
    }

    @Override
    public void doubleClick(DoubleClickEvent event) {
        if (editor == null) {
            return;
        }
        if (linkingWithEditorEnabled) {
            editor.setFocus();
            // selection itself is already handled by single click
            return;
        }
        ISelection selection = event.getSelection();
        editor.openSelectedTreeItemInEditor(selection, true);
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        if (!linkingWithEditorEnabled) {
            return;
        }
        if (ignoreNextSelectionEvents) {
            return;
        }
        ISelection selection = event.getSelection();
        editor.openSelectedTreeItemInEditor(selection, false);
    }

    public void onEditorCaretMoved(int caretOffset) {
        if (!linkingWithEditorEnabled) {
            return;
        }
        ignoreNextSelectionEvents = true;
        if (contentProvider instanceof BashEditorTreeContentProvider) {
            BashEditorTreeContentProvider gcp = (BashEditorTreeContentProvider) contentProvider;
            Item item = gcp.tryToFindByOffset(caretOffset);
            if (item != null) {
                StructuredSelection selection = new StructuredSelection(item);
                getTreeViewer().setSelection(selection, true);
            }
        }
        ignoreNextSelectionEvents = false;
    }

    public void rebuild(BashScriptModel model) {
        if (model == null) {
            return;
        }
        contentProvider.rebuildTree(model);

        TreeViewer treeViewer = getTreeViewer();
        if (treeViewer != null) {
            Control control = treeViewer.getControl();
            if (control == null || control.isDisposed()) {
                return;
            }
            treeViewer.setInput(model);
        }
    }

    class ToggleLinkingAction extends Action {

        private ToggleLinkingAction() {
            if (editor != null) {
                linkingWithEditorEnabled = editor.getPreferences().isLinkOutlineWithEditorEnabled();
            }
            setDescription("link with editor");
            initImage();
            initText();
        }

        @Override
        public void run() {
            linkingWithEditorEnabled = !linkingWithEditorEnabled;

            initText();
            initImage();
        }

        private void initImage() {
            setImageDescriptor(linkingWithEditorEnabled ? getImageDescriptionForLinked() : getImageDescriptionNotLinked());
        }

        private void initText() {
            setText(linkingWithEditorEnabled ? "Click to unlink from editor" : "Click to link with editor");
        }

    }
    
    class ToggleAlphabeticalSortAction extends Action {

        private BashEditorViewerComparator comparator;

        private ToggleAlphabeticalSortAction() {
            comparator = new BashEditorViewerComparator();

            sortOutlineAlphabeticalEnabled= BashEditorPreferences.getInstance().isSortAlphabeticalInOutlineEnabled();

            setDescription("Sort alphabetical");
            setImageDescriptor(IMG_DESC_ALPHABETICAL_SORT);
            initSelectionState();
            initText();
        }

        @Override
        public void run() {
            sortOutlineAlphabeticalEnabled = !sortOutlineAlphabeticalEnabled;

            initText();
            initSelectionState();
        }

        private void initSelectionState() {
            setChecked(sortOutlineAlphabeticalEnabled);
            if (sortOutlineAlphabeticalEnabled) {
                viewer.setComparator(comparator);            
            }else {
                viewer.setComparator(null);
            }
        }

        private void initText() {
            setText(sortOutlineAlphabeticalEnabled ? "Click to see origin ordering" : "Click to sort alphabetical");
        }

    }

    protected ImageDescriptor getImageDescriptionForLinked() {
        return IMG_DESC_LINKED;
    }

    protected ImageDescriptor getImageDescriptionNotLinked() {
        return IMG_DESC_NOT_LINKED;
    }

}
