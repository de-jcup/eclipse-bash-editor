package de.jcup.basheditor.callhierarchy;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.jcup.basheditor.workspacemodel.SharedBashModel;

public class BashCallHierarchyEntry {

    BashCallHierarchyEntry rootEntry;
    Object element;
    List<BashCallHierarchyEntry> children = null;
    BashCallHierarchyEntry parent;
    BashCallType type;
    private BashCallHierarchyCalculator calculator;
    SharedBashModel model;
    private int column;
    private int line;
    private int pos;
    private IResource resource;

    public BashCallHierarchyEntry(BashCallHierarchyEntry parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent may be not null");
        }
        this.parent = parent;
        this.rootEntry=parent.rootEntry;
        this.calculator = parent.calculator;
        this.model = parent.model;
    }
    
    public BashCallHierarchyEntry getRootEntry() {
        return rootEntry;
    }

    
    /**
     * Represents a root / initial parent
     * @param model
     * @param calculator
     */
    public BashCallHierarchyEntry(SharedBashModel model, BashCallHierarchyCalculator calculator) {
        this.parent = null;
        this.rootEntry=this;
        this.calculator = calculator;
        this.model = model;
    }

    public Object getElement() {
        return element;
    }

    /**
     * Set element - determines item which does the call. E.g. an item as element with type function represents a function. Or it could be the resource itself 
     * @param element
     * @return
     */
    public BashCallHierarchyEntry setElement(Object element) {
        this.element = element;
        return this;
    }

    public List<BashCallHierarchyEntry> getChildren(IProject projectScope) {
        if (children == null) {
            /* lazy find */
            children = calculator.findChildren(model, this, projectScope);
        }
        return children;
    }

    public BashCallHierarchyEntry getParent() {
        return parent;
    }

    public BashCallHierarchyEntry setColumn(int column) {
        this.column = column;
        return this;
    }
    
    public int getColumn() {
        return column;
    }
    
    public BashCallHierarchyEntry setLine(int row) {
        this.line = row;
        return this;
    }
    
    public int getLine() {
        return line;
    }
    
    public BashCallHierarchyEntry setPos(int pos) {
        this.pos= pos;
        return this;
    }
    
    public int getPos() {
        return pos;
    }

    /**
     * Set resource for this call hierarchy entry
     * @param resource
     */
    public void setResource(IResource resource) {
       this.resource=resource;
    }
    
    public IResource getResource() {
        return resource;
    }
}
