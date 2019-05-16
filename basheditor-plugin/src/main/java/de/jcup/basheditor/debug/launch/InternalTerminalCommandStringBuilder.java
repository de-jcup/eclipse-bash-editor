package de.jcup.basheditor.debug.launch;

public class InternalTerminalCommandStringBuilder {

    public String build(TerminalLaunchContext context) {
        if (context==null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (context.isSwitchToWorkingDirNecessary()) {
            sb.append("cd ");
            sb.append(context.getUnixStyledWorkingDir());
            sb.append(";");
        }
        String fileName = null;
        if (context.file!=null) {
            fileName=context.file.getName();
        }
        sb.append("./" + fileName);
        if (context.params!=null) {
            sb.append(" ");
            sb.append(context.params);
        }
        sb.append(";");
        sb.append("_exit_status=$?");
        sb.append(";");
        sb.append("echo \"Exit code=$_exit_status\"");
        sb.append(";");
        if (context.waitAlways) {
            sb.append("read -p \"Press enter to continue...\"");
        } else if (context.waitOnErrors) {
            sb.append("if [ $_exit_status -ne 0 ]; then read -p \"Unexpected exit code:$_exit_status , press enter to continue\";fi");
        }
        return sb.toString();
    }
}
