# Bug: when a here-doc is used the validatior may not find an error
# when a curly bracket is used inside because the content 
# of here-doc is just input not code...
# 
# See https://github.com/de-jcup/eclipse-bash-editor/issues/78
# 
# Official definition of here-doc inside bash scripting can be found at
# http://tldp.org/LDP/abs/html/here-docs.html
cat << EOF
free text{
sometimes even snippets of code..
# No highlight here
if [ -z "${MY_VAR}" ]; then
  echo "empty var"
fi
EOF
