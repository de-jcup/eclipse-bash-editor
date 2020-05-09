#!/bin/bash 

PAARMSS="paramss"
MyValue="-error"

if [[ "$MyValue" =~ ^-e.* ]]
then
echo "start with '-e'"
fi

if [[ "${PAARMSS[$((idx+1))]}" =~ ^[[:space:]]{.$ ]]
then
echo Here
fi
