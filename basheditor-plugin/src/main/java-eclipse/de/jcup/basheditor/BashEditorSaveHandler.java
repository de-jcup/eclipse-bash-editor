package de.jcup.basheditor;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.InternalHandlerUtil;
import org.eclipse.ui.internal.SaveableHelper;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.handlers.AbstractSaveHandler;

import de.jcup.basheditor.process.BashEditorFileProcessContext;
import de.jcup.basheditor.process.OutputHandler;
import de.jcup.basheditor.process.SimpleProcessExecutor;

public class BashEditorSaveHandler extends AbstractSaveHandler {

	/**
	 * The constructor.
	 */
	public BashEditorSaveHandler() {
		//registerEnablement();
		BashEditorUtil.logInfo("TEST ctor");
	}

	protected BashEditor getSaveableEditor(ExecutionEvent event) {

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);

		if (activePart instanceof BashEditor) {
			return (BashEditor) activePart;
		}

		return null;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		BashEditorUtil.logInfo("TEST execute");
		BashEditor editor = getSaveableEditor(event);

		if (editor == null) {
			return null;
		}

		if (!editor.isDirty()) {
			return null;
		}

		
		//see https://www.eclipse.org/forums/index.php/t/94609/
		IFile ifile = ((IFileEditorInput) editor.getEditorInput()).getFile();
		IPath location = ifile.getLocation();
		if (location == null)
			return null;
		
		// create aux objects
		java.io.File file = location.toFile();
		BashEditorFileProcessContext ctx = new BashEditorFileProcessContext(file);

		ExternalToolCommandArrayBuilder externalTool = new ExternalToolCommandArrayBuilder();
		String[] cmd_args = externalTool.build("beautysh.py -f $filename", file);

		// now run external tool
		SimpleProcessExecutor executor = new SimpleProcessExecutor(OutputHandler.NO_OUTPUT, false, 10 /* max 10 secs */);
		try {
			if (executor.execute(ctx, ctx, ctx, cmd_args) == 0) {
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IWorkbenchPage page = editor.getSite().getPage();
		page.saveEditor(editor, false);

		return null;
	}

	@Override
	protected EvaluationResult evaluate(IEvaluationContext context) {
		
		BashEditorUtil.logInfo("TEST evaluate");

		IWorkbenchWindow window = InternalHandlerUtil.getActiveWorkbenchWindow(context);
		// no window? not active
		if (window == null)
			return EvaluationResult.FALSE;
		WorkbenchPage page = (WorkbenchPage) window.getActivePage();

		// no page? not active
		if (page == null)
			return EvaluationResult.FALSE;

		// get saveable part
		ISaveablePart saveablePart = getSaveablePart(context);
		if (saveablePart == null)
			return EvaluationResult.FALSE;

		if (saveablePart instanceof ISaveablesSource) {
			ISaveablesSource modelSource = (ISaveablesSource) saveablePart;
			if (SaveableHelper.needsSave(modelSource))
				return EvaluationResult.TRUE;
			return EvaluationResult.FALSE;
		}

		if (saveablePart != null && saveablePart.isDirty())
			return EvaluationResult.TRUE;

		return EvaluationResult.FALSE;
	}

}
