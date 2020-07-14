package de.jcup.basheditor.callhierarchy;

import java.util.ArrayList;
import java.util.List;

public class BashCallHierarchyModel {

    List<BashCallHierarchyEntry> list = new ArrayList<>();

    public List<BashCallHierarchyEntry> getRootElements(){
        return list;
    }
}
