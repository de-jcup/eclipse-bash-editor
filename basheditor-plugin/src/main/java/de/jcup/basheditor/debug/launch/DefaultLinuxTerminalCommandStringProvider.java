package de.jcup.basheditor.debug.launch;

public class DefaultLinuxTerminalCommandStringProvider implements DefaultTerminalCommandStringProvider {

    /// bash -c x-terminal-emulator -e bash --login -c 'cd /home/albert/dev/projects/sechub/sechub-open-source/sechub-integrationtest;./integrationtest-server.sh start;_exit_status=$?;echo "Exit code=$_exit_status";if [ $_exit_status -ne 0 ]; then read -p "Unexpected exit code:$_exit_status , press enter to continue";fi' &

    @Override
    public String getDefaultTerminalCommandString() {
        return "bash -c x-terminal-emulator -e bash --login -c '"+TerminalCommandVariable.CMD_CALL.getVariableRepresentation()+"' &";
	}

}