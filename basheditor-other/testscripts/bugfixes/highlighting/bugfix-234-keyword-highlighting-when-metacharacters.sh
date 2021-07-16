# INFO:
# This is not really a bash file, but only an example for syntax highlighting...
# The example just uses the keywords "read" and "exit" to show it ...
# see https://github.com/de-jcup/eclipse-bash-editor/issues/234


read exit
read ;exit
read; exit
read ; exit

## additional parts: '|', '&', ';', '(', ')', '<', or '>'. are also meta characters
read|exit # |
read&exit # &
read;exit # ;
read(exit) # (
read(exit)read # (
read(exit)read # )
read<exit> # <
read<exit>read # <
read<exit>read # >
