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
package de.jcup.basheditor.debug;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashFileExtensionMatcher;
import de.jcup.basheditor.FileExtensionExtractor;
import de.jcup.basheditor.debug.element.AbstractBashDebugElement;
import de.jcup.basheditor.debug.element.BashVariable;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashDebugModelLabelProvider extends LabelProvider implements IDebugModelPresentation {
	private static final Image IMAGE_BASH_VARIABLE = EclipseUtil.getImage("icons/bash-editor.png", BashEditorActivator.getDefault());
    private static final String FALLBACK_DETAIL_VALUE = "";
	private BashFileExtensionMatcher matcher = new BashFileExtensionMatcher();
	private FileExtensionExtractor extractor = new FileExtensionExtractor();
	
	public void setAttribute(String attribute, Object value) {
	}

	public Image getImage(Object element) {
		if (element instanceof BashVariable) {
			return IMAGE_BASH_VARIABLE;
		}
		if (element instanceof AbstractBashDebugElement) {
			return null;
		}
		/* return null will use defaults */
		return null;
	}

	public String getText(Object element) {
		/* return null will use defaults */
		return null;
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = FALLBACK_DETAIL_VALUE;
		try {
			detail = value.getValueString();
		} catch (DebugException e) { 
			EclipseUtil.logError("Cannot get value as string:"+value,e, BashEditorActivator.getDefault());
		}
		listener.detailComputed(value, detail);
	}

	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
		}
		if (element instanceof LocalFileStorage) {
		    LocalFileStorage lfs = (LocalFileStorage) element;
		  
		    File file = lfs.getFile();
		    if (file==null) {
		        return null;
		    }
		    IFileStore fileStore;
            try {
                fileStore= EFS.getStore(file.toURI());
                return new FileStoreEditorInput(fileStore);
            }catch(Exception e) {
                EclipseUtil.logError("Was not able to get file store for file:"+file, e, BashEditorActivator.getDefault());
            }
		}
		return null;
	}

	public String getEditorId(IEditorInput input, Object element) {
	    if (element instanceof BashLineBreakpoint) {
            return BashEditor.EDITOR_ID;
        }
	    if (element instanceof IFileEditorInput) {
	        IFileEditorInput fei = (IFileEditorInput) element;
	        element = fei.getFile();
	    }
	    if (element instanceof LocalFileStorage) {
	        LocalFileStorage lfs = (LocalFileStorage) element;
            File file = lfs.getFile();
            if (file==null) {
                return null;
            }
            String fileExtension = extractor.extractFileExtension(file);
            if (matcher.isMatching(fileExtension)){
                return BashEditor.EDITOR_ID;
            }else {
                return null;
            }
            
        }
	    if (element instanceof IFile) {
	        IFile file = (IFile) element;
	        String fileExtension = file.getFileExtension();
	        if (matcher.isMatching(fileExtension,false)){
	            return BashEditor.EDITOR_ID;
	        }else {
	            return null;
	        }
		}
		return null;
	}
}
