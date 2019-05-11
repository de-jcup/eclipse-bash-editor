/*
 * Copyright 2019 Albert Tregnaghi
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
