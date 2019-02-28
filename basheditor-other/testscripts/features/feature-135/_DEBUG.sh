exec 33<>/dev/tcp/localhost/33333
function _________DEBUG_TRAP ()
{
	local _________DEBUG_COMMAND
	read -u 33 _________DEBUG_COMMAND
	eval $_________DEBUG_COMMAND
}
set -o functrace
trap _________DEBUG_TRAP DEBUG

