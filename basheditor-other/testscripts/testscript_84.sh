#!/bin/bash 

for i in `seq 1 16`; do
if [ -d "/disk$i" ]; then
  if [ ! -d "/disk$i/vicads" ]; then
    echo "Creating /disk$i/vicads"
  fi
fi
done