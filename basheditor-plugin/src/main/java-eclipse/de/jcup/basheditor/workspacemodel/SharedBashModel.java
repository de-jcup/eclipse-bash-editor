package de.jcup.basheditor.workspacemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.jcup.basheditor.script.BashFunction;
import de.jcup.basheditor.script.BashScriptModel;

/**
 * This is ONE model for all. Means it will contain information for any 
 * @author albert
 *
 */
public class SharedBashModel {
    private Map<IResource, BashScriptModel> sharedMap = new HashMap<IResource, BashScriptModel>();

    public void update(IResource resource, BashScriptModel scriptModel) {
        sharedMap.put(resource, scriptModel);
    }

    public void remove(IResource resource) {
        sharedMap.remove(resource);
    }
    
    public List<SharedModelMethodTarget> findResourcesHavingMethods(String functionName, IProject projectScope){
        List<SharedModelMethodTarget> list = new ArrayList<SharedModelMethodTarget>();
        if (functionName==null) {
            return list;
        }
        for (IResource resource: sharedMap.keySet()) {
            if (projectScope!=null) {
                boolean notInProjectScope = ! projectScope.equals(resource.getProject());
                if (notInProjectScope){
                    continue;
                }
            }
            BashScriptModel scriptModel = sharedMap.get(resource);
            if (scriptModel==null) {
                continue;
            }
            addFirstFunctionFoundInResource(list, functionName, resource, scriptModel);
        }
        return list;
    }

    private void addFirstFunctionFoundInResource(List<SharedModelMethodTarget> targets, String functionName, IResource resource, BashScriptModel scriptModel) {
        Collection<BashFunction> functions = scriptModel.getFunctions();
        for (BashFunction function: functions) {
            if (functionName.contentEquals(function.getName())){
                SharedModelMethodTarget target = new SharedModelMethodTarget(resource,function);
                targets.add(target);
                /* we break on first function found here */
                break;
            }
        }
    }
	

}
