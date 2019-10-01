#!/bin/bash 

# Here some examples from https://www.linuxjournal.com/content/bash-quoting


a="hello world"
a='hello world'

a="hello \"there\" world"
a='hello "there" world'
a='hello \'there\' world'    # causes an error
a="hello 'there' world"

b="there"
a='hello \"$b\" world'       # a is >>hello \"$b\" world
a="hello \"$b\" world"       # a is >>hello "there" world

b='"there"'
a='"hello" '$b' "world"'     # a is: >>"hello" "there" "world"<<