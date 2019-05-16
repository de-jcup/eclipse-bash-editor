package de.jcup.basheditor.debug.launch;

public class TerminalCommandStringBuilder {

    public StringBuilder build(TerminalLaunchContext context) {
        StringBuilder sb = new StringBuilder();
        if (context.isSwitchToWorkingDirNecessary()) {
            sb.append("cd ");
            sb.append(context.getUnixStyledWorkingDir());
            sb.append(";");
        }
        sb.append("./" + context.file.getName());
        sb.append(" " + context.params);

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
        sb.append("'");
        return sb;
    }
}
