
declare -A TitleMap

if [ ${#TitleMap[*]} -eq 0 ]
then
    displayerr "Map is empty"
    exit 1
fi