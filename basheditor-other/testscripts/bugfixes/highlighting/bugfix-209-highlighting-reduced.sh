#!/bin/bash
# script makes no sense - but enough for highlight test:
a=$('s/.*[\[')  # comment
b=$('s/.*[\[(')  # comment
c=$('s/.*[\[(]\([^,]*\),.*/\1/')  # Extract the 'min' version value from the '[min,max]' or '(min,max)' range.
d=$('s/[^\,]*\,\([^])]*\).*/\1/')  # Extract the 'max' version value from the '[min,max]' or '(min,max)' range.

echo "I am not highlighted correctly!"

