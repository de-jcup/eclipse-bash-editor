#!/bin/bash

## inside heredoc this is not a function
cat << -EOT
st.getval();
-EOT

abc.xyz(){
	echo "this is really a function!"
}

abc.xyz