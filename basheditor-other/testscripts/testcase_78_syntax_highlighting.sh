# As mentioned in https://github.com/de-jcup/eclipse-bash-editor/issues/78
# An heredoc block include a multiline block of text that should be probably treated as a literal string.
# 
# Official definition of here-doc inside bash scripting can be found at
# http://tldp.org/LDP/abs/html/here-docs.html
cat << EOF
free text
sometimes even snippets of code..

# No highlight here
if [ -z "${MY_VAR}" ]; then "
  echo "empty var"
fi
EOF

# highlight here
if [ -z "${MY_VAR}" ]; then
  echo "empty var"
fi

# Commands might also follow the heredoc string delimiter 
# and you would probably want to have syntax highlighting there, e.g.
## -- sorry currently not working because of the standard token handling
## in eclipse. Not easy to implement... maybe in future
cat << EOF | grep -i TargetString --color
asdf
EOF

## the next lines do have a double quote - but its not a "string" 
## because its inside a here-doc block, so this is a real reason 
## to have a special syntax highlighting for here-doc parts!
cat << EOF
free text
sometimes "even snippets of code.."
EOF
a = "hello world"
echo "Hello world"