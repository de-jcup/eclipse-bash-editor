package de.jcup.basheditor.callhierarchy;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class BashCallHierarchyEntry {

    BashCallHierarchyEntry rootEntry;
    Object element;
    List<BashCallHierarchyEntry> children = null;
    BashCallHierarchyEntry parent;
    BashCallType type;
    private BashCallHierarchyCalculator calculator;
    private int column;
    private int line;
    private int offset;
    private IResource resource;
    private int length;
    private boolean recursion;

    public BashCallHierarchyEntry(BashCallHierarchyEntry parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent may be not null");
        }
        if (parent.calculator==null) {
            throw new IllegalStateException("parent has no calculator!");
        }
        this.parent = parent;
        this.rootEntry = parent.rootEntry;
        this.calculator = parent.calculator;
    }

    public BashCallHierarchyEntry getRootEntry() {
        return rootEntry;
    }

    /**
     * Represents a root / initial parent
     * 
     * @param model
     * @param calculator
     */
    public BashCallHierarchyEntry() {
        this.parent = null;
        this.rootEntry = this;
    }

    public Object getElement() {
        return element;
    }

    /**
     * Set element - determines item which does the call. E.g. an item as element
     * with type function represents a function. Or it could be the resource itself
     * 
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
            children = calculator.findChildren(this, projectScope);
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

    public BashCallHierarchyEntry setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Set resource for this call hierarchy entry
     * 
     * @param resource
     */
    public void setResource(IResource resource) {
        this.resource = resource;
    }

    public IResource getResource() {
        return resource;
    }

    public BashCallHierarchyEntry setLength(int length) {
        this.length = length;
        return this;
    }

    public int getLength() {
        return length;
    }

    void setCalculator(BashCallHierarchyCalculator calculator) {
        this.calculator=calculator;
    }

    public void setRecursion(boolean recursion) {
        this.recursion=recursion;
    }
    public boolean isRecursion() {
        return recursion;
    }
}
