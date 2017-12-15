#
# Testscript for syntax highlighting on here-string and here-doc
#
text="a b c"
read a b c <<<"$text
echo $a $b $c  # NOT actual code, but a string inside here-string!" # this is a comment 
echo $a $b $c # this is really actual code
echo <<< echo-by-here-string echo "followed echo normal with string"
echo "Next echo statement..."
echo <<EOF
 hello world
EOF
echo "last echo..."