warn ( ) {
    echo "$*"
}

die ( ) {
    echo
    echo "$*"
    echo
    exit 1
}
#special: also with space, but no content/space in brackts
other () {
    echo "other"
}