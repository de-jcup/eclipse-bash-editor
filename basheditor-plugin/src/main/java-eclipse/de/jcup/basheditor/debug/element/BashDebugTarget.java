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
package de.jcup.basheditor.debug.element;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.ScriptUtil;
import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.basheditor.debug.BashDebugger;
import de.jcup.basheditor.debug.BashDebugger.DebugCommand;
import de.jcup.basheditor.debug.DebugBashCodeToggleSupport;
import de.jcup.basheditor.debug.DebugEventSource;
import de.jcup.basheditor.debug.DebugEventSupport;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry;
import de.jcup.basheditor.debug.launch.BashSourceLookupParticipant;
import de.jcup.basheditor.debug.launch.TerminalLauncher;
import de.jcup.eclipse.commons.EclipseResourceHelper;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashDebugTarget extends AbstractBashDebugElement implements IDebugTarget, DebugEventSource {

    private BashDebugger debugger;

    private DebugBashCodeToggleSupport toggleSupport;

    private IProcess process;

    private ILaunch launch;

    private String filename;

    private boolean suspended;

    private BashThread bashThread;

    private IThread[] threads;

    private BashDebugTargetEventDispatchJob eventDispatchJob;

    private int port;

    private boolean stopOnStartup;

    private DebugEventSupport eventsupport = new DebugEventSupport();

    private IFile fileResource;

    private BashDocumentChangeRegistry documentChangeRegistry;

    private boolean terminated;

    private TerminalLauncher terminalLauncher;


    public BashDebugTarget(ILaunch launch, IProcess process, int port, IFile programFileResource, TerminalLauncher terminalLauncher) throws CoreException {
        super(null);

        toggleSupport = new DebugBashCodeToggleSupport(BashEditorActivator.getDefault());
        if (programFileResource == null) {
            throw new IllegalArgumentException("File resource must be not null!");
        }

        this.launch = launch;
        this.fileResource = programFileResource;
        this.port = port;
        this.stopOnStartup = Boolean.valueOf(launch.getAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP));
        this.process = process;
        this.bashThread = new BashThread(this);
        this.threads = new IThread[] { bashThread };
        this.documentChangeRegistry = BashDocumentChangeRegistry.INSTANCE;
        this.debugger = new BashDebugger(this, bashThread);
        this.terminalLauncher=terminalLauncher;
    }

    public BashThread getBashThread() {
        return bashThread;
    }

    /**
     * Starts debug session on target
     * 
     * @return <code>true</code> when session was established, otherwise
     *         <code>false</code>
     */
    public boolean startDebugSession() {
        try {
            if (!debugger.startDebugServerSession(port)) {
                return false;
            }
            IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(BashDebugConstants.BASH_DEBUG_MODEL_ID);
            if (breakpoints.length > 0) {
                debugger.markBreakPointsToggled();
            }
        } catch (Exception e) {
            EclipseUtil.logError("Problems while collecting bash breakpoints", e, BashEditorActivator.getDefault());
        }

        eventDispatchJob = new BashDebugTargetEventDispatchJob();
        eventDispatchJob.schedule();

        getBreakpointManager().addBreakpointListener(this);
        return true;

    }

    public BashDocumentChangeRegistry getDocumentChangeRegistry() {
        return documentChangeRegistry;
    }

    @Override
    public BashDebugTarget getBashDebugTarget() {
        /* override, because cannot be set by constructor on construction time */
        return this;
    }

    private IBreakpointManager getBreakpointManager() {
        return getDebugPlugin().getBreakpointManager();
    }

    private DebugPlugin getDebugPlugin() {
        return DebugPlugin.getDefault();
    }

    public IProcess getProcess() {
        return process;
    }

    public IThread[] getThreads() throws DebugException {
        return threads;
    }

    public boolean hasThreads() throws DebugException {
        return true;
    }

    public String getName() throws DebugException {
        if (filename != null) {
            return filename;
        }
        filename = "Bash Program";
        try {
            filename = getLaunch().getLaunchConfiguration().getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, "Bash");
        } catch (CoreException e) {
            EclipseUtil.logError("Unable to fetch batch programm attribute from launch configuration", e, BashEditorActivator.getDefault());
        }
        return filename;
    }

    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
        String modelIdentifier = breakpoint.getModelIdentifier();
        if (modelIdentifier.equals(BashDebugConstants.BASH_DEBUG_MODEL_ID)) {
            return true;
        }
        return false;
    }

    public ILaunch getLaunch() {
        return launch;
    }

    public boolean canTerminate() {
        return !isTerminated();
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void terminate() throws DebugException {
        if (terminated) {
            return;
        }
        terminated = true;
        debugger.sendCommand(DebugCommand.TERMINATE);
        for (IThread t : threads) {
            t.terminate();
        }
        launch.terminate();
        
        /* destroy current terminal ...*/
        terminalLauncher.removeOldTerminalsOfPort(port);
        
        
    }

    public boolean canResume() {
        return !isTerminated() && isSuspended();
    }

    public boolean canSuspend() {
        return !isTerminated() && !isSuspended();
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void resume() throws DebugException {
        debugger.sendCommand(DebugCommand.RESUME);
    }

    public void resumed(int detail) {
        suspended = false;
        eventsupport.fireResumeEvent(bashThread, detail);
    }

    public void suspended(int detail) {
        suspended = true;
        eventsupport.fireSuspendEvent(bashThread, detail);
    }

    public void suspend() throws DebugException {
        debugger.sendCommand(DebugCommand.SUSPEND);
    }

    public void breakpointAdded(IBreakpoint breakpoint) {
        if (supportsBreakpoint(breakpoint)) {
            debugger.sendCommand(DebugCommand.BREAKPOINT_TOGGLED);
        }
    }

    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
        if (supportsBreakpoint(breakpoint)) {
            debugger.sendCommand(DebugCommand.BREAKPOINT_TOGGLED);
        }
    }

    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
        if (supportsBreakpoint(breakpoint)) {
            debugger.sendCommand(DebugCommand.BREAKPOINT_TOGGLED);
            try {
                if (breakpoint.isEnabled()) {
                    breakpointAdded(breakpoint);
                } else {
                    breakpointRemoved(breakpoint, null);
                }
            } catch (CoreException e) {
            }
        }
    }

    public boolean canDisconnect() {
        return eventDispatchJob != null && eventDispatchJob.getState() == Job.RUNNING;
    }

    public void disconnect() throws DebugException {
        if (eventDispatchJob == null) {
            return;
        }
        eventDispatchJob.cancel();
    }

    public boolean isDisconnected() {
        return !debugger.isConnected();
    }

    public boolean supportsStorageRetrieval() {
        return false;
    }

    public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
        return null;
    }

    /**
     * Notification we have connected to the VM and it has started. Resume the VM.
     */
    private void started() {
        eventsupport.fireCreationEvent(this);
        installDeferredBreakpoints();
    }

    /**
     * Install breakpoints that are already registered with the breakpoint manager.
     */
    private void installDeferredBreakpoints() {
        IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(BashDebugConstants.BASH_DEBUG_MODEL_ID);
        for (int i = 0; i < breakpoints.length; i++) {
            breakpointAdded(breakpoints[i]);
        }
    }

    private void terminated() {
        try {
            process.terminate();
        } catch (DebugException e) {
            EclipseUtil.logError("Was not able to terminat process", e, BashEditorActivator.getDefault());
        }
        suspended = false;
        getBreakpointManager().removeBreakpointListener(this);
        eventsupport.fireTerminateEvent(this);
    }

    protected IStackFrame[] getStackFrames() throws DebugException {
        if (debugger == null) {
            return new IStackFrame[0];
        }
        return debugger.getStackFrames();
    }

    protected void step() throws DebugException {
        debugger.sendCommand(DebugCommand.STEP);
    }

    protected void stepOver() throws DebugException {
        debugger.sendCommand(DebugCommand.STEP_OVER);
    }

    protected void stepReturn() throws DebugException {
        debugger.sendCommand(DebugCommand.STEP_RETURN);
    }

    private class BashDebugTargetEventDispatchSurveilance implements Runnable {

        private BashDebugTargetEventDispatchJob job;
        private IProgressMonitor monitor;

        private BashDebugTargetEventDispatchSurveilance(BashDebugTargetEventDispatchJob job, IProgressMonitor monitor) {
            this.job = job;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (!isTerminated()) {
                if (monitor.isCanceled()) {
                    try {
                        job.cancel();
                    } catch (Exception e) {
                        BashEditorUtil.logError("Was not able to cancel running dispatch job", e);
                    }
                }
            }
        }

    }

    private static final IProgressMonitor NO_PROGRESSMONITOR = new NullProgressMonitor();

    private class BashDebugTargetEventDispatchJob extends Job {
        private File startFile;

        public BashDebugTargetEventDispatchJob() {
            super("Bash Event Dispatch, port=" + port);
            setSystem(true);
        }

        @Override
        protected void canceling() {
            super.canceling();
            finalDisconnectAndRestoreOriginCode(NO_PROGRESSMONITOR, Status.CANCEL_STATUS, startFile);
        }

        protected IStatus run(IProgressMonitor monitor) {
            IStatus status = Status.OK_STATUS;
            String originCode = null;
            try {
                startFile = EclipseResourceHelper.DEFAULT.toFile(fileResource);
                originCode = ScriptUtil.loadScript(startFile);
            } catch (Exception e) {
                return new Status(IStatus.ERROR, BashEditorActivator.getDefault().getPluginID(), "Bash script loading failed for :" + fileResource, e);
            }

            try {
                String debugCode = toggleSupport.enableDebugging(originCode, "localhost", port);
                ScriptUtil.saveScript(startFile, debugCode);
                /*
                 * reload content in editor BEFORE debugging. Important because editor otherwise
                 * has problems with first break point (source change...)
                 */
                fileResource.refreshLocal(IResource.DEPTH_ZERO, monitor);
                BashSourceLookupParticipant.loadLookupSource();

                Thread t = new Thread(new BashDebugTargetEventDispatchSurveilance(this, monitor), "bash-debug-surveilance-" + port);
                t.start();

                debugger.connect();
                if (!debugger.isTerminated()) {
                    started();
                    debugger.process(stopOnStartup);
                }
                debugger.reset();
            } catch (Exception e) {
                boolean needsErrorLog = true;
                if (e instanceof BindException) {
                    needsErrorLog = false;
                    return new Status(IStatus.ERROR, BashEditorActivator.getDefault().getPluginID(), "Bash debug target session binding failed for port:" + port, e);
                } else if (e instanceof SocketException) {
                    /* this happens even scripts do exit with 0 because input stream read fails, so just ignore */
                    needsErrorLog = false;
                }
                if (needsErrorLog) {
                    EclipseUtil.logError("Debugger problem occurred", e, BashEditorActivator.getDefault());
                }
            } finally {
                status = finalDisconnectAndRestoreOriginCode(monitor, status, startFile);
            }
            return status;
        }

        private IStatus finalDisconnectAndRestoreOriginCode(IProgressMonitor monitor, IStatus status, File startFile) {
            status = restorOriginCode(monitor, status, startFile);
            terminated();
            try {
                debugger.disconnect();
            } catch (Exception e1) {
                EclipseUtil.logError("Unable to finally disconnect bash connector", e1, BashEditorActivator.getDefault());
            }
            debugger.markTerminated();

            try {
                BashSourceLookupParticipant.saveLookupSource();
            } catch (IOException e1) {
                EclipseUtil.logError("Unable to save lookup source", e1, BashEditorActivator.getDefault());
            }
            
            return status;
        }

        private IStatus restorOriginCode(IProgressMonitor monitor, IStatus status, File startFile) {
            String originCode;
            try {
                /*
                 * if somebody has changed the code in mean time we just reload again and toggle
                 * only off!
                 */
                String debugCode = ScriptUtil.loadScript(startFile);
                originCode = toggleSupport.disableDebugging(debugCode);
                ScriptUtil.saveScript(startFile, originCode);

                fileResource.refreshLocal(IResource.DEPTH_ZERO, NO_PROGRESSMONITOR);

            } catch (Exception e) {
                status = new Status(IStatus.ERROR, BashEditorActivator.getDefault().getPluginID(), "Removing temp bash script debugging parts failed for :" + fileResource, e);
            }
            return status;
        }

    }

    public void sendCommand(DebugCommand command) {
        if (debugger == null) {
            return;
        }
        debugger.sendCommand(command);

    }

    public void lock() {
        if (debugger == null) {
            return;
        }
        debugger.lock();

    }

    public void unlock() {
        if (debugger == null) {
            return;
        }
        debugger.unlock();

    }

    public IVariable[] createBashVariables(BashStackFrame frame) {
        if (debugger == null) {
            return new IVariable[] {};
        }
        return debugger.createBashVariables(frame);
    }

    public IValue getValue(String expression, IDebugElement element) throws Exception {
        if (debugger == null) {
            return null;
        }
        return debugger.getValue(expression, element);
    }

    public IFile getFileResource() {
        return fileResource;
    }

}
