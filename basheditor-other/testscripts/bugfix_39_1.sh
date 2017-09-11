function array-to-string {
    local _e="$( declare -p ${1} )"
    eval  "local -A _params"=${_e#*=}
    local key
    local separator
    
    for key in ${!_params[@]}
    do
        echo -n "$separator$key=${_params[$key]}"
        separator=" "
    done
}
