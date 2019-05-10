package de.jcup.basheditor.workspacemodel;

import org.eclipse.core.resources.IResource;

import de.jcup.basheditor.script.BashFunction;
import static java.util.Objects.*;

public class SharedModelMethodTarget {

        private BashFunction function;
        private IResource resource;
        
        
        public SharedModelMethodTarget(IResource resource, BashFunction function) {
            requireNonNull(resource, "resource may not be null!");
            requireNonNull(function, "function may not be null!");
            
            this.resource = resource;
            this.function = function;
        }

        public IResource getResource() {
            return resource;
        }
        
        public BashFunction getFunction() {
            return function;
        }
}
