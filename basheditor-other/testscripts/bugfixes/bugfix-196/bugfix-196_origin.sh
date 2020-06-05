#!/bin/bash

cat << -EOT
String text = "Sometext " + st.getval() + " ${somevar}. This is the end";
-EOT
