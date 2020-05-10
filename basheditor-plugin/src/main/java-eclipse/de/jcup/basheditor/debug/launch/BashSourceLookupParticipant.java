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
package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.element.BashStackFrame;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/**
 * For easier access this participant has many static methods. Static so easier
 * to use and also source look up can shared between different debug targets as
 * well.
 *
 */
public class BashSourceLookupParticipant extends AbstractSourceLookupParticipant {

	private static Hashtable<String, IFile> lookupSource = new Hashtable<String, IFile>();
	private static Hashtable<IPath, String> reverseLookupSource = new Hashtable<IPath, String>();
	private static final ReentrantLock lock = new ReentrantLock();

	public String getSourceName(Object object) throws CoreException {
		if (object instanceof BashStackFrame) {
			return ((BashStackFrame) object).getSourceFileName();
		}
		return null;
	}

	public Object[] findSourceElements(Object object) throws CoreException {
		Object[] ret = null;
		if (object instanceof BashStackFrame) {
			BashStackFrame frame = (BashStackFrame) object;
			String sourceName = frame.getSourceFileName();
			IFile file = getLookupSourceItem(sourceName);
			if (file == null) {
				IContainer folder = frame.getBashDebugTarget().getFileResource().getParent();
				if (sourceName.indexOf("./") == 0) {
					sourceName = sourceName.substring(2);
				}
				file = (IFile) folder.findMember(sourceName);
				if (file != null) {
					putLookupSourceItem(frame.getSourceFileName(), file);
					file = getLookupSourceItem(frame.getSourceFileName());
				}
			}
			if (file != null)
				ret = new Object[] { file };
			else
				ret = super.findSourceElements(object);
		} else
			ret = super.findSourceElements(object);
		return ret;
	}

	static void lock() {
		try {
			lock.lock();
		} catch (Exception e) {
			EclipseUtil.logError("Was not able to lock!", e, BashEditorActivator.getDefault());
		}
	}

	static void unlock() {
		try {
			lock.unlock();
		} catch (Exception e) {
			EclipseUtil.logError("Was not able to unlock!", e, BashEditorActivator.getDefault());
		}
	}

	public static void putLookupSourceItem(String frameFileSource, IFile file) {
		lock();
		lookupSource.put(frameFileSource, file);
		reverseLookupSource.put(file.getFullPath(), frameFileSource);
		unlock();
	}
	public static IFile getLookupSourceItem(String frameFileSource) {
		return getLookupSourceItem(frameFileSource,null);
	}
		
	public static IFile getLookupSourceItem(String frameFileSource, IContainer container) {
		IFile file;
		lock();
		file = lookupSource.get(frameFileSource);
		unlock();
		if (file == null) {
			if (container==null) {
				container = ResourcesPlugin.getWorkspace().getRoot();
			}
			IPath path = new Path(frameFileSource);
			try {
				file = container.getFile(path);
			} catch ( Exception e) {
				EclipseUtil.logError("Was not able to determin root file", e, BashEditorActivator.getDefault());

			}
			if (file != null && file.exists()) {
				putLookupSourceItem(frameFileSource, file);
			} else {
				file = null;
			}
		}
		return file;
	}

	public static String getReverseLookupSourceItem(IPath path) {
		String frameFileSource;
		lock();
		frameFileSource = reverseLookupSource.get(path);
		unlock();
		return frameFileSource;
	}

	public static File getLookupSourceFile() {
		BashEditorActivator activator = BashEditorActivator.getDefault();
		if (activator == null) {
			return null;
		}
		IPath stateLocation = activator.getStateLocation();
		if (stateLocation == null) {
			return null;
		}
		File pluginState = stateLocation.toFile();
		if (pluginState == null) {
			return null;
		}
		String rootPath = (pluginState.getAbsolutePath() + File.separator).replaceAll("\\\\", "/");
		File file = new File(rootPath + "LookupSource.hashtable");
		return file;
	}

	@SuppressWarnings("unchecked")
	public static void saveLookupSource() throws IOException {
		File file = getLookupSourceFile();
		if (file == null) {
			return;
		}
		@SuppressWarnings("rawtypes")
		Hashtable hash = new Hashtable();
		lock();
		Object[] keys = lookupSource.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			hash.put(keys[i], lookupSource.get(keys[i]).getFullPath().toOSString());
		}
		unlock();
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(hash);
		oos.flush();
		oos.close();
	}

	public static void loadLookupSource() throws IOException, ClassNotFoundException {
		File fileHash = getLookupSourceFile();
		if (fileHash == null) {
			return;
		}
		if (!fileHash.exists()) {
			return;
		}

		try (FileInputStream fis = new FileInputStream(fileHash); ObjectInputStream oin = new ObjectInputStream(fis);) {
			@SuppressWarnings("rawtypes")
			Hashtable hash = (Hashtable) oin.readObject();
			Object[] keys = hash.keySet().toArray();
			lock();
			lookupSource = new Hashtable<String, IFile>();
			reverseLookupSource = new Hashtable<IPath, String>();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (int i = 0; i < keys.length; i++) {
				Path path = new Path((String) hash.get(keys[i]));
				IFile file = root.getFile(path);
				if (file != null) {
					lookupSource.put((String) keys[i], file);
					reverseLookupSource.put(path, (String) keys[i]);
				}
			}
		}
		unlock();
	}

}
