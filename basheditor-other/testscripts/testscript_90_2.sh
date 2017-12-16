get_full_xtlcmd() {
    local xtlcmd=$(get_act_xtlcmd $2 "$1" "$3" "$4" "$5")
    local pkg lop lreqver p rop rreqver branch x pkgURL subpkg
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    p=" $2"
    if [ "$2" == "EVERYTHINGS" ]; then
        p=""
    fi
    pkgURL=$(get_full_URL "$1" "$2" "$3" "$4" "$5")
    [ -n "$pkgURL" ] && p=" $pkgURL"
}