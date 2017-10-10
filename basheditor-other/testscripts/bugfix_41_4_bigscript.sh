#!/bin/bash
##############################################################
#
#      UCA Installer common definitions
#
##############################################################

export PYTHONUNBUFFERED=1

#when executed from a non-terminal do not define colors and cursor motions
if [ -t 2 ]
then
    BLACK=$(tput setaf 0)
    RED=$(tput setaf 1)
    GREEN=$(tput setaf 2)
    YELLOW=$(tput setaf 3)
    LIME_YELLOW=$(tput setaf 190)
    POWDER_BLUE=$(tput setaf 153)
    BLUE=$(tput setaf 4)
    MAGENTA=$(tput setaf 5)
    CYAN=$(tput setaf 6)
    WHITE=$(tput setaf 7)
    BRIGHT=$(tput bold)
    NORMAL=$(tput sgr0)
    BLINK=$(tput blink)
    REVERSE=$(tput smso)
    UNDERLINE=$(tput smul)
    RIGHT=$(tput cuf 1)
    LEFT=$(tput cub 1)
    SAVE=$(tput sc)
    REST=$(tput rc)
    CLEAR=$(tput el)
fi

# display the text parameter on screen and in log file
# $1 the text to display
function display {
    printf "%s" "$1" | tee -a $LOG_FILE
    echo >> $LOG_FILE
}

# display the text parameter on screen and in log file
# $1 the text width
# $2 the text to display
function displayw {
    printf "%-$1s" "$2" | tee -a $LOG_FILE
    echo >> $LOG_FILE
}

function displaynl {
    printf "%s\n" "$1" | tee -a $LOG_FILE
}

function displayerr {
    printf "\n${RED}%-79s${NORMAL}\n" "$1" | tee -a $LOG_FILE
}

function displaywarn {
    printf "\n${YELLOW}%-79s${NORMAL}\n" "$1" | tee -a $LOG_FILE
}

function displayok {
    printf "[${GREEN}%s${NORMAL}]\n" "DONE" | tee -a $LOG_FILE
}

function displayfail {
    printf "[${RED}%s${NORMAL}]\n" "FAILED" | tee -a $LOG_FILE
}

function echogreen {
    printf "${GREEN}%s${NORMAL}\n" "$1" | tee -a $LOG_FILE
}

# ask the user to enter some input
# $1 is the invite text to be displayed
# $2 is a default value (optional when not in 'auto' mode)
# $3 is the max invite text width
function ask {

    def=""
    if [ "$2" != "" ]
    then
        def=$(printf "${NORMAL}[${GREEN}%s${NORMAL}]" "$2")
    fi
    text=`printf "${YELLOW}%-$3s %s: ${NORMAL}" "$1" "$def"`
    display "$text"

    if [ "$AUTOMODE" == "true" -a "$2" == "" ]
    then
        # in auto mode default values are mandatory
        displayerr "In 'auto' mode, default values are mandatory"
        exit 1
    fi 
    if  [ "$AUTOMODE" == "true" ]
    then
        displaynl
        REPLY=$2
    else
        read REPLY
        if [ -z "$REPLY" ]
        then
            REPLY=$2
        fi
    fi
    return 0
}

# ask the user to enter some input with no echo of default value
# $1 is the invite text to be displayed
# $2 is a default value (optional)
# $3 is the max invite text width
function askSecret {
    text=$(printf "$1")
    if [ "$2" != "" ]
    then
        def=$(printf "${NORMAL}[${GREEN}********${NORMAL}]" "$2")
    fi
    display `printf "\n${YELLOW}%-$3s %s:${NORMAL}" "$text" "$def"` 
    read -s REPLY
    if [ -z $REPLY ]
    then
        REPLY=$2
    fi
    return 0
}

# run a shell command displaying some dots while the command is running
# $@ is the command to execute with its arguments
function doshell {
    local status=0
    command="$@"

    exec=/tmp/uca_install_$RANDOM
    cat > $exec <<EOF

printf "[$SAVE"
REWIND="$LEFT"
tail -f $LOG_LAST_FILE 2> /dev/null | while true
do
  read  line
 
      if [[ \$line == "quit" ]] ; then
        printf "$REST\$REWIND$CLEAR"
        break
      else
        if echo "\$line" | grep -q "PLAY \["
        then
          printf "$REST$CLEAR.$SAVE]"
          REWIND="\$REWIND$LEFT"
        else
          if echo "\$line" | grep -q "TASK \["
          then
            nline=\$(echo "\$line"| sed "s/.*\[//g"| sed "s/-.*//g")
            printf "$REST$CLEAR.$SAVE]"
            REWIND="\$REWIND$LEFT"
          fi
        fi
      fi

done 
EOF
    bash $exec & PID=$!
    trap "kill -0 $PID &> /dev/null && kill -9 $PID &> /dev/null" EXIT
    # use "$@" instead of $command here to preserve arguments with blank
    "$@" &>> $LOG_LAST_FILE 
    status=$?
    echo "quit" >> $LOG_LAST_FILE
    # wait for the forked process to terminate
    sleep 0.1
    kill -0 $PID &> /dev/null && kill -9 $PID  &> /dev/null
    wait $PID  &> /dev/null
    rm -f $exec

    if [ $status -ne 0 ]; then
        displayfail
        echo
        printf "${RED}\n"                                              
        echo "error with -> $command" >&2
        echo
        printf "${YELLOW}Latest failing task is:\n"
        printf "======================${NORMAL}\n"
        tac $LOG_LAST_FILE | grep 'TASK ' -m 1 -B 9999 | tac |grep -v "quit" 
        printf "${NORMAL}(Full log file available in $LOG_LAST_FILE)\n"
        printf "${YELLOW}\n"
        echo "Once the problem is fixed, please execute the same command again." >&2
        printf "${NORMAL}\n"
        cat $LOG_LAST_FILE >> $LOG_FILE
        exit $status
    fi
    cat $LOG_LAST_FILE >> $LOG_FILE
    displayok
    return $status
}

function doansible {
    local pwd=`pwd`
    local package=`basename $pwd`
    INVENTORY_FILE=$INSTALLER_DATADIR/packages/$package/inventory/hosts
    PLAYBOOK_FILE=$1
    shift
    echo [`date`] > $LOG_LAST_FILE
    echo ansible-playbook -i ${INVENTORY_FILE} ${PLAYBOOK_FILE} "$@" -e INSTALLER_DIR=$INSTALLER_DIR $ANSIBLEOPTS -vv -s >> $LOG_LAST_FILE
    doshell ansible-playbook -i ${INVENTORY_FILE} ${PLAYBOOK_FILE} "$@" -e INSTALLER_DIR=$INSTALLER_DIR $ANSIBLEOPTS -vv -s 
}


# Start an installation phase based on an ansible script
# $1 is the name of the phase
# $2 is the name of the ansible script to execute
function runAnsible {
    displayw 73 "$1"
    shift
    doansible "$@"
}

# Start an installation phase based on a shell script
# $1 is the name of the phase
# other parameters represent the command to execute and its arguments
function runShell {
    displayw 73 "$1"
    shift
    echo [`date`] > $LOG_LAST_FILE
    echo TASK ["$@"] >> $LOG_LAST_FILE
    doshell "$@"
}

# This procedure returns a host list (separated by blank)
# form the given host file
# $1 is the section [section]
# $2 the host file name
function get-host-list {
    if [ -f $2 ]
    then
        insection="false"
        separator=""
        OIFS=$IFS
        _host_list=""
        while IFS='= ' read host options
        do
            if [[ $host == \[*] ]]
            then
                if [ $host == \[$1] ]
                then 
                    insection="true"
                else
                    insection="false"
                fi
                continue
            fi
            if [ "$insection" == "true" ]
            then
                _host_list=${_host_list}"$separator$host"
                separator=" "
            fi
        done < $2
        IFS=$OIFS
        echo -n $_host_list
    fi
}

#This procedure builds a list with a format as expected by Yaml (for the with-items for example)
# from a String list given as parameter
# $1 name of the built list
# $2 the list 
function build-ansibleparameter-list {
    _YAML_PARAM_LIST="{\"$1\": ["
    SEP=""
    for _item in $2 
    do
        _YAML_PARAM_LIST="${_YAML_PARAM_LIST}${SEP}$_item"
        SEP=", "
    done
    _YAML_PARAM_LIST="${_YAML_PARAM_LIST}]}"
    echo $_YAML_PARAM_LIST
}

# this procedure updates an ansible host file with a set of key values passed as parameters
# $1 is the file name
# $2 is the section name [section] the represents the context
# $3 is the host to add or update
# $4 is the key/value array
function update-host-file {    
        local file=$1
        local section=$2
        local __host=$3

        value=`array-to-string $4`
        if [ ! -f "$file" ]
        then
            mkdir -p `dirname $file`    
            touch $file
        fi
        awk "BEGIN {updated=0}
        /^\[$section]/ {ok=1;print \$0; next}
        ok==1 && \$1==\"$__host\" {next}
        /^\[/ && ok==1 {print \"$__host $value\"; ok=0; updated=1}
        {print \$0}
    END {if (updated==0) if (ok==1) print \"$__host $value\"; else print \"[$section]\n$__host $value\"}" $file > /tmp/`basename ${file}`.$$
        mv /tmp/`basename ${file}`.$$ $file
}

# this procedure removes the host entry for the given context from host file
# $1 is the file name
# $2 is the section name [section] the represents the context
# $3 is the host to remove
function remove-from-host-file { 
        local file=$1
        local section=$2
        local host=$3

        if [ ! -f "$file" ]
        then
            return 0
        fi
        awk "
        /^\[$section]/ {ok=1;print \$0; next}
        ok==1 && \$1==\"$host\" {next}
        {print \$0}" $file > /tmp/`basename ${file}`.$$
        mv /tmp/`basename ${file}`.$$ $file
}

# check host connectivity
# works on global variable TARGET_HOST
function check-host-connectivity {
    # check if the host is pingable
    
    # try Ipv4 ping
    /bin/ping $TARGET_HOST -c1 > /dev/null 2>&1
    if [ "$?" -ne "0" ] 
    then
        # Ipv4 ping failed, try ipv6 ping
        /usr/bin/ping6 $TARGET_HOST -c1 > /dev/null 2>&1
        if [ "$?" -ne "0" ] 
        then
            # both Ipv4 and Ipv6 ping failed
            displayerr "the entered host '$TARGET_HOST' is not a pingable host"
            return 1
        fi
    fi
    ssh -n -o BatchMode=yes ${INSTALL_USER}@${TARGET_HOST} true
    if [ "$?" -ne "0" ] 
    then
        # trying to configure ssh for this host

        # generate private key if not exist
        if [ ! -f ~/.ssh/id_ecdsa ]
        then 
            display "...configuring SSH"
            ssh-keygen -t ecdsa -N "" -f ~/.ssh/id_ecdsa &> $LOG_LAST_FILE << EOFKEYGEN
    
EOFKEYGEN
            displayok
        fi
        displaynl "...setting SSH Access to ${TARGET_HOST}"
        ssh-copy-id ${INSTALL_USER}@${TARGET_HOST}&>> $LOG_LAST_FILE 
        if [ "$?" -ne "0" ] 
        then
            displayfail
            displayerr "failed to propagate/install public key to host ${TARGET_HOST} for user ${INSTALL_USER}"
            return 1;
        fi
        ssh -o BatchMode=yes ${INSTALL_USER}@${TARGET_HOST} true
        if [ $? -ne 0 ]
        then
            displayfail
            displayerr "ssh connection failed with ${INSTALL_USER}@${TARGET_HOST}"
            return 1
        else
            displayok
            return 0
        fi
   fi
}

# This procedure and the host givent as parameter to the host file for the given context
# the ansible connectivity is checked
# $1 is the host to add
# $2 is the Context
# $3 is the host file
function add-host-and-check-ansible {
    local _addedHost=$1
    local _context=$2
    local _hostFile=$3

    hostList=`get-host-list $_context $_hostFile`
    hostNotFound=true
    for host in $hostList
    do
        if [ "$host" == "$_addedHost" ]
        then
            hostNotFound="false"
            break;
        fi
    done
    if [ $hostNotFound == "true" ]
    then
        local -A hostSettings
        hostSettings[InstallStatus]="NotInstalled"
        hostSettings[ansible_ssh_user]="$INSTALL_USER"
        update-host-file $_hostFile ${_context} $_addedHost hostSettings || { displayerr "Failed to update $HOST_FILE host file."; exit 1; }
        # check that ansible is working on the host 
        runShell "...checking hosts: checking Ansible on $_addedHost" /usr/bin/ansible -i $_hostFile $_context -l "${_addedHost}," -m ping || { displayerr "ansible check failed! fix ansible configuration and try again"; exit 1; }     
    fi
}

# This procedure allows the user to enter a host
# the ansible reachability is checked.
# the host file is updated with the new host.
# $1 is the message to display while getting host
# $2 is the host file 
# $3 is the section in host file
# As a result this procedures set the TARGET_HOST variable
function enter-host {
    local WELCOME=$1
    local HOSTS_FILE=$2
    local SECTION=$3
    
    VALID_HOSTNAME="false"
    while [ "$VALID_HOSTNAME" == "false" ]
    do
        VALID_HOSTNAME="true"
        ERRMSG=""
               
        ask "$WELCOME" 
        
        if [[ "$REPLY" =~ ^\ *$ ]]
        then
            TARGET_HOST=""
        else
            TARGET_HOST=`echo $REPLY | xargs` # Trim the reply string
        fi

        if [ "$TARGET_HOST" != "" ]
        then
            # check if the host connectivity
            check-host-connectivity
            if [ $? -ne 0 ]
            then
                ERRMSG="Cannot connect to $TARGET_HOST host."
                VALID_HOSTNAME="false"
            fi
        else
            VALID_HOSTNAME="false"
        fi
       
        if [ "$VALID_HOSTNAME" == "false" -a "$ERRMSG" != "" ]
        then
            displayerr "$ERRMSG"
        fi
    done

    # if host is new, Update the server hosts file and check ansible access
    add-host-and-check-ansible $TARGET_HOST $SECTION $HOSTS_FILE
    return $?
}

# this procedure returns the list of all contexts contained in all inventory files
function get-context-list-from-hosts-files {
    ls ${INSTALLER_DATADIR}/packages/*/inventory/hosts > /dev/null 2>&1
    if [ $? -eq 0 ]
    then
        sed '
/^\[/! d
s/^\[\(.*\)\]$/\1/' ${INSTALLER_DATADIR}/packages/*/inventory/hosts | sort | uniq | xargs
    fi
}

# display the Title of the given package
# $1 is the package name
function display-title {

    displaynl ""
    displaynl "-----------------------------------------------------------------------"

    Idx=${PackageIdxMap[$1]}
    if [ "$Idx" -ne 0 ]
    then
        if [ "${Aliases[$Idx]}" != "" ]
        then
            displaynl "Package: [$1] (${Aliases[$Idx]}) ${TitleMap[$Idx]} ${VersionMap[$Idx]}"
        else
            displaynl "Package: [$1] ${TitleMap[$Idx]} ${VersionMap[$Idx]}"
        fi
    else
        displaynl "Package: [$1]"
    fi
}

# This function display the parameters from an input file
# $1 the input file name
# $2 is the context [section] 
function display-param-from-file {
    if [ -f $1 ]
    then
        while read _context
        do
            if [ "$_context" == "[$2]" ]
            then
                while read key value
                do
                    if [[ $key =~ \[.* ]]
                    then
                        break
                    else
                        param=`printf "\t%-70s" "    $key$value"`
                        displaynl "$param"
                    fi
                done
                break
            fi
        done < $1
    fi
}

# returns a string representing the array given as parameter as key=value list
# $1 the array
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

# returns a string representing the array given as parameter as key=value list
# $1 the array
function array-to-quoted-string {
    local _e="$( declare -p ${1} )"
    eval  "local -A _params"=${_e#*=}
    local key
    local separator
    
    for key in ${!_params[@]}
    do
        echo -n "$separator$key=\"${_params[$key]}\""
        separator=" "
    done
}

# build and associative array from a string made of xxx="yyy" value pairs
# $1 is the string to parse
# $2 is the returned associative array
function string-to-array {
    unset _theDesc
    declare -A _theDesc
    _theDesc=$2

    while read key value
    do
        if [ ! -z "$key" ]
        then
            eval $_theDesc[$key]=$value
        fi
    done  << EOF
`echo "$1" | sed -r 's/[[:alnum:]|_|-]+=/\n&/g' | tr '=' ' ' `
EOF
}

# this procedure displays the installation on a per context basis
# $1 is the CONTEXT to display (ALL for all contexts)
function display-installation {

    CONTEXT_LIST=`get-context-list-from-hosts-files`
 
    contextDisplayed=false
    for context in $CONTEXT_LIST
    do
        if [ "$context" == "$1" -o "$1" == "ALL" ]
        then
            contextDisplayed=true
            echogreen "*******************************************************************************"
            echogreen "                              CONTEXT : $context                               "
            echogreen "*******************************************************************************"
            
            for package in $INSTALLER_DATADIR/packages/*
            do
                if [ ! -f $package/inventory/hosts ]
                then
                    continue
                fi
                if grep -q "\[$context\]" $package/inventory/hosts
                then
                    while read host restOfTheline
                    do
                        if [[ $host == \[*] ]]
                        then
                            if [ $host == \[$context] ]
                            then 
                                insection="true"
                            else
                                insection="false"
                            fi
                            continue
                        fi

                        display-title `basename $package`

                        if [ "$insection" == "true" ]
                        then
                            unset -v hostSettings
                            local -A hostSettings
                            read-host-setting  $context $host  $package/inventory/hosts hostSettings
                            IStatus=${hostSettings[InstallStatus]}
                            unset -v hostSettings[InstallStatus]
                            CStatus=${hostSettings[ConfigStatus]}
                            unset -v hostSettings[ConfigStatus]
                            hostLine=`printf "\t%-30s\t%s\t%s" "$host" "$IStatus" "$CStatus"`
                            displaynl "$hostLine"
                            # display installation dirs
                            settings=`array-to-quoted-string hostSettings`
                            argsline=`printf "\t%-70s" "    $settings"`
                            displaynl "$argsline"
                            
                            #display parameters
                            package_Idx=${PackageIdxMap[`basename $package`]}
                            display-param-from-file $package/var/${InputFileMap[$package_Idx]} "$context:$host"
                        fi
                    done < $package/inventory/hosts
                fi
            done
        fi
    done
    if [ $contextDisplayed == "false" ]
    then
        displayerr "Nothing installed for '$CONTEXT' context"
    fi
}

# build a yaml key/value list from an associative array
# only the keys matching the regular expression are put to the list
# $1 is the list name
# $2 is a regular expression used for filtering
# $3 is the associative array
function build-key-value-param-list {
    regexp=$2
    _e="$( declare -p ${3} )"
    eval  "declare -A _params"=${_e#*=}
    
    _paramList="{\"$1\": ["
    SEP=""
    for param in "${!_params[@]}"
    do
        if [[ $param =~ $regexp ]]
        then
            _paramList="${_paramList}${SEP}{key: '$param', value: '${_params[$param]}'}"
            SEP=", "
        fi
    done
    _paramList="${_paramList}]}" 
    
    echo $_paramList   
}

# this procedure cleans a package output file section.
# it remove all key definitions from the given section
# $1 is the file name
# $2 is the section name [section]
function clean-output-file-section {
        file=$1
        section=$2
        if [ ! -f "$file" ]
        then
            test -d `dirname $file` || mkdir -p `dirname $file`
            touch $file
        fi
        awk -F "=" "
        /^\[$section]/ {ok=1;print \$0; next}
        /.*=.*/ && ok==1 {next}
        /^\[/ && ok==1 {ok=0;print \$0; next}
        {print \$0} " $file > /tmp/`basename ${file}`.$$
        mv /tmp/`basename ${file}`.$$ $file
}

# this procedure updates a package output file with ini format
# $1 is the file name
# $2 is the section name [section]
# $3 is the key to add or update
# $4 is the value
function update-output-file {
        file=$1
        section=$2
        key=$3
        value=$4
        if [ ! -f "$file" ]
        then
            test -d `dirname $file` || mkdir -p `dirname $file`
            touch $file
        fi
        awk -F "=" "BEGIN {updated=0}
        /^\[$section]/ {ok=1;print \$0; next}
        ok==1 && \$1==\"$key\" {next}
        /^\[/ && ok==1 {print \"$key=$value\"; ok=0; updated=1}
        {print \$0}
    END {if (updated==0) if (ok==1) print \"$key=$value\"; else print \"[$section]\n$key=$value\"}" $file > /tmp/`basename ${file}`.$$
        mv /tmp/`basename ${file}`.$$ $file
}

# returns the package output file (create it if needed)
function get-package-outputfile {
    local package=$1
    local packageIdx=${PackageIdxMap[$package]}
    local packagename=${PackageMap[$packageIdx]%%-[0-9]*}
    
    if [ -f $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]} ]
    then
        echo $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]}
    else
        test -d $INSTALLER_DATADIR/packages/$package/var || mkdir -p $INSTALLER_DATADIR/packages/$package/var
        touch $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]}
        echo $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]}
    fi
    
    return 0
}

# migrate the package input and output file to new package version
# output file is migrated by creating a symbolik link to the existing file if it exists
# whereas the input file is copied
# $1 from package
# $2 new package
function migrate-package-outputfile {
    local fromPackage=$1
    local package=$2
    local fromPackageIdx=${PackageIdxMap[$fromPackage]}
    local packageIdx=${PackageIdxMap[$package]}
    
    test -d $INSTALLER_DATADIR/packages/$package/var || mkdir -p $INSTALLER_DATADIR/packages/$package/var
    
    if [ -f $INSTALLER_DATADIR/packages/$fromPackage/var/${OutputFileMap[$fromPackageIdx]} ]
    then
        ln -sf $INSTALLER_DATADIR/packages/$fromPackage/var/${OutputFileMap[$fromPackageIdx]} $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]}
    fi

    touch $INSTALLER_DATADIR/packages/$package/var/${OutputFileMap[$packageIdx]}
    
    if [ -f $INSTALLER_DATADIR/packages/$fromPackage/var/${InputFileMap[$fromPackageIdx]} ]
    then
        cp $INSTALLER_DATADIR/packages/$fromPackage/var/${InputFileMap[$fromPackageIdx]} $INSTALLER_DATADIR/packages/$package/var/${InputFileMap[$packageIdx]}
    fi
    return 0
}

# stores the output parameters of an installation to the package output file
# such parameters can be used as input parameters of some other package installation
# $1 is the package to store the output for
# $2 is the section
# $3 is an associative array parameter Map
function store-package-installation-output {
    _e="$( declare -p ${3} )"
    eval  "declare -A outputParameters"=${_e#*=}

    local package=$1
    local section=$2
    
    local packageIdx=${PackageIdxMap[$package]}
    if [  -z "${OutputFileMap[$packageIdx]}" ]
    then
        displayerr "ERROR:  the descriptor file of the package $package should define the 'OutputFile' parameter"
        exit 1
    fi
    
    # get the package output file
    local outputFile=`get-package-outputfile $package`
    if [ $? -ne 0 ]
    then
        displayerr "ERROR store-package-installation-output: $package is not a a valid package name"; exit 1
    fi
    
    # cleanup current section
    clean-output-file-section $outputFile ${section}
    
    # stores the installation output
    for key in ${!outputParameters[@]}
    do
        update-output-file $outputFile ${section} $key "${outputParameters[$key]}"
    done
}

# This procedure retrieves the given package installation output 
# for the given context and returns it as an associative array
# if the output file does not exits the array is returned empty
# $1 is the package to get the output from
# $2 is the section
# $3 is the associative array
function retrieve-package-installation-output {
    local package=$1
    local section=$2
    local -A _theDesc
    _theDesc=$3
    local outputFile=`get-package-outputfile $package`
    if [ ! $? -eq 0 ]
    then
        displayerr "ERROR retrieve-package-installation-output: $package is not a a valid package name"; exit 1
    fi

    # The case of a non-existing output file is a valid case.
    # the array is returned empty
    [ "$outputFile" == "" ] && return 0

    OIFS=$IFS
    IFS="="
    insection="false"
    while read line
    do
        if [[ $line == \[*] ]]
        then
            if [ "$line" == "[$section]" ]
            then
                insection="true"
            else
                insection="false"
            fi
            continue
        else
            if [ "$insection" == "true" ]
            then
                set $line
                eval $_theDesc[$1]="'$2'"
            fi
        fi
    done < $outputFile
    IFS=$OIFS
}



# returns all packages that depends on the given package
# $1 is the package
# $2 the host name
function get-dependent-packages {
    {
        local package=$1
        local host=$2
        
        # get the current index of the given package
        local _Idx
        local _package
        local package_status
        for _Idx in "${!PackageMap[@]}"
        do
            if [ "${DependenciesOnSameHostMap[$_Idx]}" != "" ]
            then
                for _package in ${DependenciesOnSameHostMap[$_Idx]}
                do
                    # if the package is version compliant
                    if is-package-compliant $_package $package
                    then
                        package_status=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/${PackageMap[$_Idx]}/inventory/hosts`
                        if [ "$package_status" == "Installed" ]
                        then
                            echo ${PackageMap[$_Idx]}
                        fi
                    fi
                done  
            fi
            
            if [ "${DependenciesAnywhereMap[$_Idx]}" != "" ]
            then
                for _package in ${DependenciesAnywhereMap[$_Idx]}
                do
                    # if the package is version compliant
                    if is-package-compliant $_package $package
                    then
                        for host in `get-host-list $CONTEXT $INSTALLER_DATADIR/packages/${PackageMap[$_Idx]}/inventory/hosts`
                        do
                            package_status=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/${PackageMap[$_Idx]}/inventory/hosts`
                            if [ "$package_status" == "Installed" ]
                            then
                                echo ${PackageMap[$_Idx]}
                                break
                            fi
                        done
                    fi
                done 
            fi
        done
    } | uniq | xargs
}

# This procedure builds a configuration dependency list. this is the ordered list of
# dependent packages
# $1 the package to install
# $2 the host name
# Setting Global Array DependentPackages
function build-list-of-dependent-packages {
    local host=$2
    local _Idx

    DEPENDENT_PACKAGE_LIST=`get-dependent-packages $1 $host`
    if [ "$DEPENDENT_PACKAGE_LIST" != "" ]
    then
        local _package
        for _package in $DEPENDENT_PACKAGE_LIST
        do
            # is this package not already in the list ?
            if [[ ! "${DependentPackages[@]}" =~ .*$_package.* ]]
            then
                build-list-of-dependent-packages $_package $host
                #add the package to the list
                if [ ${#DependentPackages[@]} -gt 0 ]
                then
                    _Idx=${#DependentPackages[@]}
                    while [ $_Idx -gt 0 ]
                    do
                       DependentPackages[$_Idx]=${DependentPackages[$((_Idx-1))]}
                       _Idx=$((_Idx-1))
                    done
                fi
                DependentPackages[0]=$_package
            fi
        done
    fi

}

# This procedure configures a package.
# when this procedure is called we consider the installation phase was performed
# the function calls in turn the configuration of the dependent packages
# $1 the package to install
# $2 the target host 
function configure-package {

    Package=$1
    TARGET_HOST=$2
    PACKAGE_DIR=$INSTALLER_DIR/packages/$Package
    
    if [ ! -d $PACKAGE_DIR ]
    then
        displayerr "$Package is not a a valid package name"; exit 1
    fi
       
    # perform the configuration in a sub-shell to preserve local variables
    (
        # change to package directory
        cd $PACKAGE_DIR
        
        local -A hostSettings
        read-host-setting $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/$Package/inventory/hosts hostSettings
        
        # Set BIN_DIR_ROOT and DATA_DIR_ROOT variables
        setBinDataDirs $Package hostSettings

        
        Package_Idx=${PackageIdxMap[$Package]}
        # check if the Configuration script exits
        # if not. no configuration is needed

        if [ -z "${ConfigScriptMap[$Package_Idx]}" ]
        then
            return 0
        fi

        displaynl ""
        displaynl "Configuring $Package on: $TARGET_HOST"
        displaynl "" 
              
        # do the configuration only if the package is correctly installed.
        package_status=`get-host-installation-status $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/$Package/inventory/hosts`
        if [ "$package_status" != "Installed" ]
        then
            displayerr "The package $Package must be correctly installed on host $TARGET_HOST before tying to configure it"; exit 1
        fi
        
        # call the Configuration script
        ( . $INSTALLER_DIR/packages/${Package}/${ConfigScriptMap[$Package_Idx]} ${Package} "${TARGET_HOST}"; )
        ConfigStatus=$?
    
        if [ $ConfigStatus -ne 0 ]
        then
            # when here consider the configuration failed
            hostSettings[ConfigStatus]="Failed"
            update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || displayerr "Failed to update $HOST_FILE host file."
            return $ConfigStatus
        else
            # when here consider the configuration succeeded
            hostSettings[ConfigStatus]="Configured"
            update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || displayerr "Failed to update $HOST_FILE host file."
        fi
      
    )
}

# propagate configuration changes to dependent packages
# $1 the parent package
# $2 the host name
function configure-dependent-packages {
    local Package=$1
    local _package
    local host=$2
    declare -A DependentPackages
    
    unset DependentPackages
    build-list-of-dependent-packages $Package $host
    
    if [ ${#DependentPackages[@]} -ne 0 ]
    then
        displaynl
        displaynl "propagate configuration changes to dependent packages..."
        displaynl

        # the Configuration propagation is done in automatic reply mode
        AUTOMODE="true"
        
        for _package in ${DependentPackages[@]}
        do
            for host in `get-host-list $CONTEXT $INSTALLER_DATADIR/packages/$_package/inventory/hosts`
            do
                package_status=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/$_package/inventory/hosts`
                if [ "$package_status" == "Installed" ]
                then
                    # re-configuring the dependent package. Re-do the configuration with last values
                    configure-package ${_package}  $host
                    if [ $? -ne 0 ]
                    then
                        # change propagation failed
                        displayerr "Failed to propagate configuration changes to dependent packages"
                        return 1
                    fi         
                fi
            done
        done    
    fi
    return 0
}

# this procedure removes duplicated patches in the list
# keep patch with different base name, and with the highest version 
# $1 string containing the patch package list
function sanitized-patch-list {

        local packageList=$1
        local fullPackageName

        # example: the given patch list is "abc-def-4.0-0001A ghi-jkl-5.0-00001A abc-def-4.0-0002A xyz-1.0 uvw_3-6.5" 

        echo $packageList | tr ' ' '\n' | while read fullPackageName
        do
                #split basename and version
                echo ${fullPackageName%%-[0-9]**} ${fullPackageName#${fullPackageName%%-[0-9]**}}
        done | sort -r | awk 'BEGIN {package=""}
        {if (package!=$1) print $1$2; package=$1; next}'

        # at this stage the list given as example is:
        # xyz-1.0
        # uvw_3-6.5
        # ghi-jkl-5.0-00001A
        # abc-def-4.0-0002A
}

# installs patches for the specified package if any are available
# $1 the package for which to install the patches
# $2 the target host
function install-patch-if-any {
        local _package=$1

        # get the patch packages to install if any
        if [ ! -z "${RelatedPatchList[$_package]}" ]
        then
                local _patchPackage
                for  _patchPackage in `sanitized-patch-list "${RelatedPatchList[$_package]}"`
                do
                        install-package $_patchPackage $2
                done
        fi
        return $?
}

# This procedure install a package.
# it ask for the host list where the package has to be installed
# if not already given as parameter
# set the installation status based on the return status of the installation script
# $1 the package to install
# $2 the target host (Optional)
function install-package {

    # perform the installation in a sub-shell to preserve local variables
    (
        Package=$1
        PACKAGE_DIR=$INSTALLER_DIR/packages/$Package
        
        if [ ! -d $PACKAGE_DIR ]
        then
            displayerr "$Package is not a a valid package name"; exit 1
        fi
    
        # change to package directory
        cd $PACKAGE_DIR
        
        Package_Idx=${PackageIdxMap[$Package]}
        # check if the host is given as parameter. if not the ask the user to enter it
        if [ "$2" == "" ]
        then
            enter-host "Enter the fully qualified host name on which to install ${TitleMap[$Package_Idx]}" $INSTALLER_DATADIR/packages/$Package/inventory/hosts ${CONTEXT}
        else
            TARGET_HOST="$2"
            check-host-connectivity
            if [ $? -ne 0 ]
            then 
                # cannot connect to host
                return 1
            fi
            add-host-and-check-ansible $TARGET_HOST $CONTEXT $INSTALLER_DATADIR/packages/$Package/inventory/hosts
            if [ $? -ne 0 ]
            then 
                # cannot run ansible on host
                return 1
            fi
        fi

        #check package installation status. install the package only if a previous version is not already.
        local InstallationStatus=`getSimilarPackageInstallationStatus $Package $TARGET_HOST`
        case $InstallationStatus in
        InstalledNotCompliant)
            displayerr "A package incompatible with this update is already installed"; exit 1
            ;;
        InstalledCompliant)
            displayerr "A package compatible with this update is already installed. Use the Update Option to install this package"; exit 1
            ;;
        NotInstalled)
            # go down
            ;;
        esac

        #check and install dependencies
        CheckAndInstallOrUpdateDependencies $Package "$TARGET_HOST" install-package
        if [ $? -ne 0 ]
        then
             displayerr "Failed to install package $Package dependencies"
             return 1
        fi
        
        displaynl ""
        displaynl "Installing $Package to: $TARGET_HOST"
        displaynl "" 

        local -A hostSettings
        read-host-setting $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/${Package}/inventory/hosts hostSettings
                
        # Set BIN_DIR_ROOT and DATA_DIR_ROOT variables
        setBinDataDirs $Package hostSettings
        
        hostSettings[BIN_DIR_ROOT]=${BIN_DIR_ROOT}
        hostSettings[DATA_DIR_ROOT]=${DATA_DIR_ROOT}
        hostSettings[InstallStatus]="NotInstalled"
        update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || { InstallationStatus=$?; displayerr "Failed to update $HOST_FILE host file."; exit $InstallationStatus;}
                          
        # call the package installation script
        ( . $INSTALLER_DIR/packages/${Package}/${InstallScriptMap[$Package_Idx]} ${Package} "${TARGET_HOST}"; )
        InstallationStatus=$?
    
        if [ $InstallationStatus -ne 0 ]
        then
            # when here consider the installation failed
            hostSettings[InstallStatus]="Failed"
            runShell "...Updating host installation status" update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || { displayerr "Failed to update $HOST_FILE host file."; exit $InstallationStatus;}
            return $InstallationStatus
        else
            # when here consider the installation succeeded
            hostSettings[InstallStatus]="Installed"
            runShell "...Updating host installation status" update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || { displayerr "Failed to update $HOST_FILE host file.";  exit 1;}
        fi
        
        configure-package $Package $TARGET_HOST || { InstallationStatus=$?; displayerr "Failed to configure $Package "; exit $InstallationStatus;}
      
        install-patch-if-any $Package $TARGET_HOST  || { InstallationStatus=$?; displayerr "Failed to install $Package last patch."; exit $InstallationStatus;}
        
        configure-dependent-packages $Package $TARGET_HOST || { InstallationStatus=$?; displayerr "Failed to configure dependent packages"; exit $InstallationStatus;}
        
        return $InstallationStatus
    )
}

# This procedure removes a package, then
# reset the installation status based on the return status of the uninstallation script
# $1 the package to remove
# $2 the target host
function remove-package {

    # perform the installation in a sub-shell to preserve local variables
    (
        Package=$1
        TARGET_HOST="$2"
        PACKAGE_DIR=$INSTALLER_DIR/packages/$Package
        
        
        if [ ! -d $PACKAGE_DIR ]
        then
            displayerr "$Package is not a valid package name"; exit 1
        fi
    
        Package_Idx=${PackageIdxMap[$Package]}
    
        # change to package directory
        cd $PACKAGE_DIR
        
        #check package installation status
        package_status=`get-host-installation-status $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/$Package/inventory/hosts`
        if [ "$package_status" != "Installed" ]
        then
            displayerr "$Package is not installed on $TARGET_HOST"; exit 1
        fi

        # remove script check
        if [ "${RemoveScriptMap[$Package_Idx]}" == "" ]
        then
            displayerr "Failed to remove the package. the RemoveScript entry in desc file does not exist"; exit 1
        fi

        #check given host connectivity
        check-host-connectivity
        if [ $? -ne 0 ]
        then 
            # cannot connect to host
            return 1
        fi

        if [  "$FORCEOPT" == "false" ]
        then
            #check dependencies (no other packages than patch packages should depend on it)
            unset DependentPackages
            build-list-of-dependent-packages $Package $TARGET_HOST
            
            if [ ${#DependentPackages[@]} -ne 0 ]
            then
                #count the patch packages
                local numberOfPatchPackages=0
                for dependentPackage in ${DependentPackages[@]}
                do
                    dependentPackageIdx=${PackageIdxMap[$dependentPackage]}
                    if [ "${IsAPatchFor[$dependentPackageIdx]}" != "" ]
                    then
                        numberOfPatchPackages=$((numberOfPatchPackages+1))
                    fi
                done
                if [ ${#DependentPackages[@]} -ne $numberOfPatchPackages ]
                then
                    displayerr "$Package can't be removed. The following packages are depending on it:"
                    for dependentPackage in ${DependentPackages[@]}
                    do
                        dependentPackageIdx=${PackageIdxMap[$dependentPackage]}
                        if [ "${IsAPatchFor[$dependentPackageIdx]}" == "" ]
                        then
                            # if not a patch package, display it
                            displayerr "    - $dependentPackage"
                        fi
                    done
                    exit 1
                fi
            fi
        fi

        # remove patch packages if any
        if [ ! -z "${RelatedPatchList[$Package]}" ]
        then
            local _patchPackage
            for _patchPackage in ${RelatedPatchList[$Package]}
            do
                _patchPackage_status=`get-host-installation-status $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/$_patchPackage/inventory/hosts`
                if [ "$_patchPackage_status" == "Installed" ]
                then
                    remove-package $_patchPackage $TARGET_HOST
                fi
            done
        fi

        displaynl ""
        displaynl "Erasing $Package from: $TARGET_HOST"
        displaynl "" 
        
        local -A hostSettings
        read-host-setting $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/${Package}/inventory/hosts hostSettings
                
        # Set BIN_DIR_ROOT and DATA_DIR_ROOT variables
        setBinDataDirs ${Package} hostSettings

        
        ( . $INSTALLER_DIR/packages/${Package}/${RemoveScriptMap[$Package_Idx]} ${Package} ${TARGET_HOST}; )
        RemoveStatus=$?
        
        # if succes remove the entry in host file
        if [ $RemoveStatus -eq 0 ]
        then
            # when here consider the installation success
            runShell "...Updating host installation status" remove-from-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST || { displayerr "Failed to update $HOST_FILE host file."; exit $RemoveStatus;}
        fi
        
        # Call the post remove script
        if [ $RemoveStatus -eq 0 ]
        then
            if [ ! -z "${PostRemoveScriptMap[$Package_Idx]}" ]
            then
                ( . $INSTALLER_DIR/packages/${Package}/${PostRemoveScriptMap[$Package_Idx]} ${Package} ${TARGET_HOST}; )
                RemoveStatus=$?
            fi
        fi
        
        return $RemoveStatus
    )
}

# check is a package with same base name is istalled but not compatible
# $1 package name
# $2 host name
# this procedure returns :
# 'Not Installed' when not similar package is installed
# 'Installed' if this exact package is installed
# 'InstalledCompliant' if a similar package is installed and compliant
# 'InstalledNotCompliant if a similar package is installed and not compliant
function getSimilarPackageInstallationStatus {
    local package=$1
    local host=$2
    
    #check package installation status.
    local _Idx
    for _Idx in ${!PackageMap[@]}
    do
        local thisPackage=${PackageMap[$_Idx]}
        local packageBaseName=${package%%-[0-9]*}
        local thisPackageBaseName=${thisPackage%%-[0-9]*}
        
        if [  "$packageBaseName" == "$thisPackageBaseName" ]
        then
            packagestatus=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/${thisPackage}/inventory/hosts`
            if [ "$packagestatus" == "Installed" ]
            then
                if [ $package == $thisPackage ]
                then
                    echo -n "Installed"
                else
                    if is-package-compliant $thisPackage $package
                    then
                        echo -n "InstalledCompliant";
                    else
                        echo -n "InstalledNotCompliant";
                    fi
                fi
                return 0
            fi
        fi
    done
    echo -n "NotInstalled"

}

# returns the installed package similar to the one given as parameter if any (including packages implementing aliases)
# $1 package name
# $2 host name
function getSimilarInstalledPackage {
    local package=$1
    local host=$2
    
    local packageBaseName=${package%%-[0-9]*}

    #check package installation status.
    local _Idx
    for _Idx in ${!PackageMap[@]}
    do
        local thisPackage=${PackageMap[$_Idx]}
        local thisPackageBaseName=${thisPackage%%-[0-9]*}
        
        if [  "$packageBaseName" == "$thisPackageBaseName" ]
        then
            packagestatus=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/${thisPackage}/inventory/hosts`
            if [ "$packagestatus" == "Installed" ]
            then
                echo -n "${PackageMap[$_Idx]}"
                break
            fi
        fi
    done
}

# returns an Installed compliant package from its Alias name
# $1 package name
# $2 host name
function getSimilarInstalledAliasPackage {
    local package=$1
    local host=$2
    
    local packageBaseName=${package%%-[0-9]*}
    
    if isPackageAnAlias $package
    then
        for thisPackageIdx in ${!PackageMap[@]}
        do
            local alias
            for alias in ${Aliases[$thisPackageIdx]}
            do
                if is-package-compliant $package $alias
                then
                    packagestatus=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/${PackageMap[${thisPackageIdx}]}/inventory/hosts`
                    if [ "$packagestatus" == "Installed" ]
                    then
                        echo -n "${PackageMap[${thisPackageIdx}]}"
                        break
                    fi
                fi
            done
        done
    fi
}


# returns a list of similar packages
# $1 package name
function getSimilarPackageList {
    local package=$1
    
    #check package installation status.
    local _Idx
    for _Idx in ${!PackageMap[@]}
    do
        local thisPackage=${PackageMap[$_Idx]}
        local packageBaseName=${package%%-[0-9]*}
        local thisPackageBaseName=${thisPackage%%-[0-9]*}
        
        if [  "$packageBaseName" == "$thisPackageBaseName" ]
        then
            echo "${PackageMap[$_Idx]}"
        fi
    done
}

# This procedure updates a package.
# updating a package may require the update of the packages it depends on.
# $1 the package to update
# $2 the target host
function update-package {

    # perform the installation in a sub-shell to preserve local variables
    (
        Package=$1
        TARGET_HOST="$2"
        PACKAGE_DIR=$INSTALLER_DIR/packages/$Package
        
        if [ ! -d $PACKAGE_DIR ]
        then
            displayerr "$Package is not a valid package name"; exit 1
        fi
    
        Package_Idx=${PackageIdxMap[$Package]}
    
        # change to package directory
        cd $PACKAGE_DIR
                
        #check package installation status. Update the package only if a previous version already installed and is compliant.
        local InstallationStatus=`getSimilarPackageInstallationStatus $Package $TARGET_HOST`
        case $InstallationStatus in
        InstalledNotCompliant)
            displayerr "A package incompatible with this update is already installed"; exit 1
            ;;
        NotInstalled)
            # when there is no package to update, perform a full installation
            install-package $Package $TARGET_HOST
            return $?
            ;;
        Installed)
            displayerr "The package $Package is up to date"; exit 0
            ;;
        InstalledCompliant)
            # go down
            ;;
        esac
                
        # update script check
        if [ "${UpdateScriptMap[$Package_Idx]}" == "" ]
        then
            displayerr "Failed to Update the package. the UpdateScript entry in desc file does not exist"; exit 1
        fi

        UpdatedPackage=`getSimilarInstalledPackage $Package $TARGET_HOST`

        # get BIN and DATA dirs from the UpdatedPackage
        local -A hostSettings
        read-host-setting $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/${UpdatedPackage}/inventory/hosts hostSettings
                
        if [ "${hostSettings[ConfigStatus]}" != "Configured" ]
        then
            displayerr "$Package is not correctly Configured on $TARGET_HOST. it cannot be updated"; exit 1
        fi
        
        #check given host connectivity
        check-host-connectivity
        if [ $? -ne 0 ]
        then 
            # cannot connect to host
            return 1
        fi

        
        # remove UpdatedPackage patches if any before updating
        if [ ! -z "${RelatedPatchList[$UpdatedPackage]}" ]
        then
            local _patchPackage
            for _patchPackage in ${RelatedPatchList[$UpdatedPackage]}
            do
                local _patchPackage_status=`get-host-installation-status $CONTEXT $TARGET_HOST $INSTALLER_DATADIR/packages/$_patchPackage/inventory/hosts`
                if [ "$_patchPackage_status" == "Installed" ]
                then
                    remove-package $_patchPackage $TARGET_HOST
                fi
            done
        fi
        
        # Set BIN_DIR_ROOT and DATA_DIR_ROOT variables
        setBinDataDirs $UpdatedPackage hostSettings

        displaynl ""
        displaynl "Updating $Package on: $TARGET_HOST"
        displaynl "" 
        
        migrate-package-outputfile $UpdatedPackage $Package
        
        #check dependencies (must install missing dependencies or update installed one)
        CheckAndInstallOrUpdateDependencies $Package "$TARGET_HOST" update-package
        if [ $? -ne 0 ]
        then
             displayerr "Failed to update or install package $Package dependencies"
             return 1
        fi
        
        ( . $INSTALLER_DIR/packages/${Package}/${UpdateScriptMap[$Package_Idx]} ${Package} ${TARGET_HOST}; )
        UpdateStatus=$?
        
        # if succes remove the entry in host file
        if [ $UpdateStatus -eq 0 ]
        then
            # when here consider the installation success
            # The updated package is removed
            runShell "...Updating '${UpdatedPackage}' package status" remove-from-host-file $INSTALLER_DATADIR/packages/${UpdatedPackage}/inventory/hosts ${CONTEXT} $TARGET_HOST || { displayerr "Failed to update $HOST_FILE host file."; exit $UpdateStatus;}

            # The new package status is set to Installed/configured
            hostSettings[InstallStatus]="Installed"
            hostSettings[ConfigStatus]="Configured"
            runShell "...Updating '${Package}' package status" update-host-file $INSTALLER_DATADIR/packages/${Package}/inventory/hosts ${CONTEXT} $TARGET_HOST hostSettings || { displayerr "Failed to update $HOST_FILE host file."; exit $UpdateStatus;}

            # now install patches when some available
            install-patch-if-any $Package $TARGET_HOST  || { UpdateStatus=$?; displayerr "Failed to install $Package last patch."; exit $UpdateStatus;}
        fi
        
        return $UpdateStatus
    )
}

# This procedure returns an associative array containing the variables associate to a host
# for a given context
# $1 is the Context
# $2 is the host name
# $3 the host file name
# $4 the resulting associative array
function read-host-setting {
    local -A _retVals
    _retVals=$4
    local _host
    local restOfTheLine
    local insection
    
    if [ -f $3 ]
    then
        insection="false"
        while read _host restOfTheLine
        do
            if [[ $_host == \[*] ]]
            then
                if [ $_host == \[$1] ]
                then
                    insection="true"
                else
                    insection="false"
                fi
                continue
            fi
            if [ "$insection" == "true" ]
            then
                if [ "$2" == "$_host" ]
                then
                    set -- $restOfTheLine
                    for pair in $*
                    do
                        eval $_retVals[${pair/=*/}]=${pair/*=/}
                    done
                fi
            fi
        done < $3
    fi
}

# This procedure returns a host installation status for the given CONTEXT
# form the given host file
# $1 is the Context
# $2 is the host name
# $3 the host file name
function get-host-installation-status {
        local -A settings
        read-host-setting $1 $2 $3 settings

        if [ "${settings[InstallStatus]}" == "" ]
        then
                echo "NotInstalled"
        else
                echo "${settings[InstallStatus]}"
        fi
}

# compare two string where numbers a re considered as version number
# ei : xxx-1.10-SNAPSHOT > xxx-1.9-SNAPSHOT
# result is true if $1 < $2 (bu using version compare)
function versionlte {
    [  "$1" = "`echo -e "$1\n$2" | sort -V | head -n1`" ]
} 

# check the compliance of the tested package
# $1 the package
# $2 the tested package
# returns true if the tested package $2 is compliant with $1 
function is-package-compliant {
    local package=$1
    local testPackage=$2
    
    local packageBaseName
    local testPackageBasename
    local testPackageIdx
    testPackageIdx=${PackageIdxMap[$testPackage]}
    if [ "$testPackageIdx" == "" ]
    then
        # the tested package does not exist (alias ?)
        # compliance is checked assuming the minorr compatibiliy
        packageBaseName=${package%%.${package#*-+([0-9]).}}
        testPackageBasename=${testPackage%%.${testPackage#*-+([0-9]).}}
    else
        case ${VersionCompliancePolicyMap[$testPackageIdx]} in 
        exact)  #exact version Match
            packageBaseName=$package
            testPackageBasename=${testPackage}
            ;;
        major)  # basename match (Major versions are compatible)
            packageBaseName=${package%%-[0-9]*}
            testPackageBasename=${testPackage%%-[0-9]*}
            ;;
        minor|*)  # basename + Major version match (only minor versions are compatible)
            packageBaseName=${package%%.${package#*-+([0-9]).}}
            testPackageBasename=${testPackage%%.${testPackage#*-+([0-9]).}}
            ;;
        esac
    fi
    if [ ${packageBaseName} == ${testPackageBasename} ]
    then
        if versionlte $package $testPackage
        then
            return 0
        fi
    fi
    return 1
}

# returns the highest compliant package.
# the aliases are not considered
# $1 the package name. this name can be partial but cannot be an alias
function get-highest-compliant-package {
    local package=$1
    local thisPackage
    local thisPackageIdx
    for thisPackageIdx in ${!PackageMap[@]}
    do
        thisPackage=${PackageMap[$thisPackageIdx]}
        if is-package-compliant $thisPackage $package
        then
            echo $thisPackage
        fi
    done | sort -V | tail -n1
}

# This procedure checks and installs the dependencies of the given package
# $1 is the PACKAGE for which checking the dependencies
# $2 is the list on which the PACKAGE is to be installed
# $3 the command to execute (install-package or update-package)
function CheckAndInstallOrUpdateDependencies {

    local _PACKAGE=$1
    local _TARGET_HOST=$2
    local Command=$3
    
    unset NEED_INSTALL_ONSAMEHOST
    local -A NEED_INSTALL_ONSAMEHOST
    unset NEED_INSTALL_ANYWHERE
    local -A NEED_INSTALL_ANYWHERE

    unset NEED_UPDATE_ONSAMEHOST
    local -A NEED_UPDATE_ONSAMEHOST
    unset NEED_UPDATE_ANYWHERE
    local -A NEED_UPDATE_ANYWHERE
    
    local NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ONSAMEHOST="false"
    local NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ANYWHERE="false"
    local NOT_COMPLIANT_INSTALLED_PACKAGE_FOUND="false"
    
    # get the current index of the given package
    local _Idx=${PackageIdxMap[$_PACKAGE]}

    local NEED_INSTALL_SAME_HOST="false"
    local NEED_INSTALL_ANYWHERE="false"
    local NEED_UPDATE_SAME_HOST="false"
    local NEED_UPDATE_ANY_HOST="false"
    local THERE_ARE_UNAVAILABLE_PACKAGES="false"
    
    local host
    
    if [ "${DependenciesOnSameHostMap[$_Idx]}" != "" ]
    then

        displaynl "==============================================================================="
        displaynl "| Dependencies on same host:                                                  |"
        displaynl "|-----------------------------------------------------------------------------|"

        for package in ${DependenciesOnSameHostMap[$_Idx]}
        do
            #check if the package (or a package with highest version or an a package implementing this alias name) is installed on this same host
            local thisPackage
            local InstalledPackagefound=false
            thisPackage=`getSimilarInstalledPackage $package $_TARGET_HOST`
            if [ "$thisPackage" != "" ]
            then
                InstalledPackagefound=true
                if is-package-compliant $package $thisPackage
                then
                    displaynl "`printf "|     %-70s  |" $package`"
                    displaynl "`printf "|         -> %-43s %20s |" $thisPackage "Installed"`"
                else
                    local bestAvailablePackage=`get-highest-compliant-package ${package}`
                    if [ "$bestAvailablePackage" != "" ]; is-package-compliant $thisPackage $bestAvailablePackage
                    then
                        displaynl "`printf "|     %-70s  |" $package`"
                        displaynl "`printf "|         -> %-43s %20s |" "$thisPackage" "Needs Update"`"
                        displaynl "`printf "|         -> %-43s %20s |" "$bestAvailablePackage" "Available"`"
                        NEED_UPDATE_ONSAMEHOST[${bestAvailablePackage}]="true"
                        NEED_UPDATE_SAME_HOST="true"
                    else
                        displaynl "`printf "|     %-70s  |" $package`"
                        displaynl "`printf "|         -> %-40s %23s |" $thisPackage "Installed Not Compliant"`"
                        NOT_COMPLIANT_INSTALLED_PACKAGE_FOUND=true
                    fi
                fi
            fi
            
            if [ $InstalledPackagefound == false ]
            then
                if `isPackageAnAlias $package`
                then
                    thisPackage=`getSimilarInstalledAliasPackage $package $_TARGET_HOST`
                    if [  "$thisPackage" != "" ]
                    then
                        displaynl "`printf "|     %-70s  |" "$package (Alias)"`"
                        displaynl "`printf "|         -> %-43s %20s |" $thisPackage "Installed"`"
                    else
                        displaynl "`printf "|     %-50s %20s |" "A package aliasing: $package" "To Check !"`"
                        NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ONSAMEHOST="true"
                    fi
                else
                    local bestAvailablePackage=`get-highest-compliant-package ${package}`
                    if [ "$bestAvailablePackage" != "" ]
                    then
                        displaynl "`printf "|     %-50s %20s |" $package "Not Installed"`"
                        displaynl "`printf "|         -> %-43s %20s |" "$bestAvailablePackage" "Available"`"
                        NEED_INSTALL_ONSAMEHOST[${bestAvailablePackage}]="true"
                    else
                        displaynl "`printf "|     %-50s %20s |" $package "Not Available"`"
                        THERE_ARE_UNAVAILABLE_PACKAGES="true"
                    fi
                    NEED_INSTALL_SAME_HOST="true"
                fi
            fi
        done  
        displaynl "|-----------------------------------------------------------------------------|"

    fi
    
    if [ "${DependenciesAnywhereMap[$_Idx]}" != "" ]
    then
        displaynl "==============================================================================="
        displaynl "| Dependencies on any host:                                                   |"
        displaynl "|-----------------------------------------------------------------------------|"
       
        for package in ${DependenciesAnywhereMap[$_Idx]}
        do
            #check if the package (or a package with highest version or an a package implementing this alias name) is installed on this same host
            local validPackage
            local InstalledPackagefound=false
            unset perHostInstallStatus
            local -A perHostInstallStatus
            for validPackage in `getSimilarPackageList $package`
            do
                for host in `get-host-list $CONTEXT $INSTALLER_DATADIR/packages/$validPackage/inventory/hosts`
                do
                    local thisPackage=`getSimilarInstalledPackage $package $host`
                    if [ "$thisPackage" != "" -a "${perHostInstallStatus[$host]}" != "installed" ]
                    then
                        InstalledPackagefound=true
                        perHostInstallStatus[$host]="installed"
                        if is-package-compliant $package $thisPackage
                        then
                            displaynl "`printf "|     %-70s  |" "$package (on $host)"`"
                            displaynl "`printf "|         -> %-43s %20s |" $thisPackage "Installed"`"
                        else
                            local bestAvailablePackage=`get-highest-compliant-package ${package}`
                            if [ "$bestAvailablePackage" != "" ]; is-package-compliant $thisPackage $bestAvailablePackage
                            then
                                displaynl "`printf "|     %-70s  |" "$package (on $host)"`"
                                displaynl "`printf "|         -> %-43s %20s |" "$thisPackage" "Needs Update"`"
                                displaynl "`printf "|         -> %-43s %20s |" "$bestAvailablePackage" "Available"`"
                                NEED_UPDATE_ANYWHERE[$host]=${bestAvailablePackage}
                                NEED_UPDATE_ANY_HOST="true"
                            else
                                displaynl "`printf "|     %-70s  |" "$package  (on $host)"`"
                                displaynl "`printf "|         -> %-40s %23s |" $thisPackage "Installed Not Compliant"`"
                                NOT_COMPLIANT_INSTALLED_PACKAGE_FOUND=true
                            fi
                        fi
                    fi
                done
            done

            if [ $InstalledPackagefound == false ]
            then
                if `isPackageAnAlias $package` 
                then
                    thisPackage=`getSimilarInstalledAliasPackage $package $_TARGET_HOST`
                    if [  "$thisPackage" != "" ]
                    then
                        displaynl "`printf "|     %-70s  |" "$package (Alias)"`"
                        displaynl "`printf "|         -> %-43s %20s |" $thisPackage "Installed"`"
                    else
                        displaynl "`printf "|     %-50s %20s |" "A package aliasing: $package" "To Check !"`"
                        NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ANYWHERE="true"
                    fi
                else
                    local bestAvailablePackage=`get-highest-compliant-package ${package}`
                    if [ "$bestAvailablePackage" != "" ]
                    then
                        displaynl "`printf "|     %-50s %20s |" $package "Not Installed"`"
                        displaynl "`printf "|         -> %-43s %20s |" "$bestAvailablePackage" "Available"`"
                        NEED_INSTALL_ANYWHERE["${bestAvailablePackage}"]="true"
                    else
                        displaynl "`printf "|     %-50s %20s |" $package "Not Available"`"
                        THERE_ARE_UNAVAILABLE_PACKAGES="true"
                    fi
                fi
                NEED_INSTALL_ANYWHERE=true
            fi
            
        done
        displaynl "|-----------------------------------------------------------------------------|"
    fi
    
    displaynl ""
    
    if [ $NOT_COMPLIANT_INSTALLED_PACKAGE_FOUND == true ]
    then
        displayerr "Some required dependencies are incompatible with the current installation! can't continue"
        return 1
    fi
    
    if [ "$THERE_ARE_UNAVAILABLE_PACKAGES" == "true" ]
    then
        displayerr "Some required dependencies are not available! can't continue"
        return 1
    fi
    
    if [ "$NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ONSAMEHOST" == "true" -o "$NO_PACKAGE_IMPLEMENTING_ALIAS_IS_INSTALLED_ANYWHERE" == "true" ]
    then
        displayerr "Some required dependencies are missing and cannot be installed automatically due to aliasing. This must be checked Manually"
        return 1
    fi
    
    if [ "$NEED_INSTALL_ANYWHERE" == "true" ]
    then
        displaynl "the following packages are missing and can be installed some where:"
        for package in ${!NEED_INSTALL_ANYWHERE[@]}
        do
            displaynl " $package"
        done
        displayerr "Install the package before restarting this installation."
        return 1
    fi
    
    if [ "$NEED_INSTALL_SAME_HOST" == "true" -o "$NEED_UPDATE_SAME_HOST" == "true" -o "$NEED_UPDATE_ANY_HOST" == "true" ]
    then
        if [ "$NEED_INSTALL_SAME_HOST" == "true" ]
        then
            for package in ${!NEED_INSTALL_ONSAMEHOST[@]}
            do
               displaynl "$package need to be installed on : $_TARGET_HOST"
            done
        fi
        
        if [ "$NEED_UPDATE_SAME_HOST" == "true" ]
        then
            for package in ${!NEED_UPDATE_ONSAMEHOST[@]}
            do
               displaynl "$package need to be updated on : $_TARGET_HOST"
            done
        fi
        
        if [ "$NEED_UPDATE_ANY_HOST" == "true" ]
        then
            for host in ${!NEED_UPDATE_ANYWHERE[@]}
            do
               displaynl "${NEED_UPDATE_ANYWHERE[$host]} need to be updated on : $host"
            done
        fi
        

        message="Do You want to perform such operations ? (Y/N)"
        ask "$message" "Y"
        case $REPLY in
        y|Y)
            if [ "$NEED_INSTALL_SAME_HOST" == "true" ]
            then
                for package in ${!NEED_INSTALL_ONSAMEHOST[@]}
                do
                    # make the package installation
                    install-package $package $_TARGET_HOST
                    if [ "$?" -ne 0 ]
                    then
                        displayerr "Error installing $package on $_TARGET_HOST"
                        exit 1
                    fi
                done
            fi
            
            if [ "$NEED_UPDATE_SAME_HOST" == "true" ]
            then
                for package in ${!NEED_UPDATE_ONSAMEHOST[@]}
                do
                    # make the package iupdate
                    update-package $package $_TARGET_HOST
                    if [ "$?" -ne 0 ]
                    then
                        displayerr "Error updating $package"
                        exit 1
                    fi
                done
            fi

            
            if [ "$NEED_UPDATE_ANY_HOST" == "true" ]
            then
                for host in ${!NEED_UPDATE_ANYWHERE[@]}
                do
                    # make the package iupdate
                    update-package  ${NEED_UPDATE_ANYWHERE[$host]} $host
                    if [ "$?" -ne 0 ] 
                    then
                        displayerr "Error updating $package"
                        exit 1
                    fi
                done
            fi
            ;;
            
        *)
            displayerr "aborting installation due to missing dependencies"
            return 1
            ;;
        esac
    fi
}

# This procedure displays the locations where this package is already installed
# $1 Is the package name
# this function uses the $CONTEXT global variable

function display-install-locations {
    package=$1
    
    displaynl "==============================================================================="
    line=`printf "|     %-70s  |" "Package $package current installation summary:"`
    displaynl "$line"
    displaynl "|-----------------------------------------------------------------------------|"
    
    oneEntryDisplayed="false"
    for host in `get-host-list $CONTEXT $INSTALLER_DATADIR/packages/$package/inventory/hosts`
    do
        oneEntryDisplayed="true"
        package_status=`get-host-installation-status $CONTEXT $host $INSTALLER_DATADIR/packages/$package/inventory/hosts`
        line=`printf "|     %-50s %20s |" $host "$package_status"`               
        displaynl "$line"
        
    done
    if [ "$oneEntryDisplayed" == "false" ]
    then
        line=`printf "|     %-70s  |" "The package $package is not installed yet"`
        displaynl "$line"
    fi
    displaynl "|-----------------------------------------------------------------------------|"
    
}

# This procedure returns reads Script file and set the specified packages's
# configuration parameters as environment variables
# $1 the input script file name
# $2 is the section [section] 
function set-params-from-scriptfile {
    if [ -f $1 ]
    then
        insection="false"
        returnedValue=""
        while read line
        do
            if [[ "$line" =~  \[.*\].* ]]
            then
                if [[ "$line" =~ ^\[$2\].* ]]
                then
                    insection="true"
                else
                    insection="false"
                fi
                continue
            fi
            if [ "$insection" == "true" ]
            then
                    eval $line
            fi
        done < $1
    fi
}

# This procedure returns a variable value from an input file
# $1 the input file name
# $2 is the section [section] 
# $3 is the variable name
function get-input-param-from-file {
    if [ -f $1 ]
    then
        insection="false"
        returnedValue=""
        while read key value
        do
            if [[ $key =~ \[.*\] ]]
            then
                if [[ $key == \[$2\] || $key == \[\] ]]
                then 
                    insection="true"
                else
                    insection="false"
                fi
                continue
            fi
            if [ "$insection" == "true" ]
            then
                if [ $key == "$3:" ]
                then
                    returnedValue=$value
                fi
            fi
        done < $1
        echo $returnedValue
    fi
}

# this procedure updates ansible variable definition file with ini format
# $1 is the file name
# $2 is the section name [section]
# $3 is the key to add or update
# $4 is the value
function update-vars-file {
        file=$1
        section=$2
        key=$3
        value=$4
        if [ ! -f "$file" ]
        then
            test -d `dirname $file` || mkdir -p `dirname $file`
            touch $file
        fi
        awk "BEGIN {updated=0}
        /^\[$section]/ {ok=1;print \$0; next}
        ok==1 && \$1==\"$key:\" {next}
        /^\[/ && ok==1 {print \"  $key: $value\"; ok=0; updated=1}
        {print \$0}
    END {if (updated==0) if (ok==1) print \"  $key: $value\"; else print \"[$section]\n  $key: $value\"}" $file > /tmp/`basename ${file}`.$$
        mv /tmp/`basename ${file}`.$$ $file
}

# This procedure ask the user to enter a parameter
# the parameter is then stored in the input parameter file
# when the parameter is already present in the parameter file
# the value is used as the default proposed value
# $1 is the package name
# $2 is the host name
# $3 invite text to display
# $4 is the variable that is set with the acquired value
# $5 is the default value (when non is read from input file)
# name of the variable 
function get-input-parameter {
    package=$1
    section="$CONTEXT:$2"
    invite="$3"
    varname=$4
    defVal=$5
    package_Idx=${PackageIdxMap[$1]}

    # check the input file is defined
    if [ "${InputFileMap[$package_Idx]}" == "" ]
    then
        displayerr "the descriptor file of the package $package should define the 'InputFile' parameter"
        exit 1
    fi
    
    infile=$INSTALLER_DATADIR/packages/$package/var/${InputFileMap[$package_Idx]}
    
    # create infile if necessary
    if [ ! -f $infile ]
    then
        test -d $INSTALLER_DATADIR/packages/$package/var || mkdir -p $INSTALLER_DATADIR/packages/$package/var
        if [ -f $INSTALLER_DIR/packages/$package/${InputFileMap[$package_Idx]} ]
        then
            cp $INSTALLER_DIR/packages/$package/${InputFileMap[$package_Idx]} $infile
        fi
    fi
            
    
    # if not defined in the environment,
    # get the default value from input file
    if [ -z "${!varname}" ]
    then
        if [ -f "$infile" ] 
        then
            defaultValue=`get-input-param-from-file $infile $section $varname`
        fi
        
        if [ "$defaultValue" == "" ]
        then
            defaultValue=$defVal
        fi
    else
        #get the value from environement
        defaultValue=`eval echo '$'$varname`
    fi

    #ask for the user input
    ask "$invite" "$defaultValue"
    eval "$varname=\"$REPLY\""

    #stores the new value in input file
    update-vars-file $infile $section $varname "${!varname}"
}


# This procedure set the BIN_DIR_ROOT and DATA_DIR_ROOT according the following
# algorithm
# - use the BIN_DIR_ROOT AND DATA_DIR_ROOT form the hostsettings array
# - if not set. try to get the values from the package descriptor file
# - if still not set. use the DEFAULT_BIN_DIR and DEFAULT_DATA_DIR_ROOT
# $1 package
# $2 array containing the actual host settings
function setBinDataDirs {
    _e="$( declare -p ${2} )"
    eval  "local -A hostSettings"=${_e#*=}
    
    #get definitions from host file
    if [ ! -z "${hostSettings[BIN_DIR_ROOT]}" ]
    then
        BIN_DIR_ROOT=${hostSettings[BIN_DIR_ROOT]}
    fi
    if [ ! -z "${hostSettings[DATA_DIR_ROOT]}" ]
    then
        DATA_DIR_ROOT=${hostSettings[DATA_DIR_ROOT]}
    fi

    #if not set get definitions from package descriptor
    Idx=${PackageIdxMap[$1]}
    [ -z "$BIN_DIR_ROOT" ] && BIN_DIR_ROOT=${DefaultInstallDirMap[Idx]}
    [ -z "$DATA_DIR_ROOT" ] && DATA_DIR_ROOT=${DefaultDataDirMap[Idx]}
    
    # else use default values
    [ -z "$BIN_DIR_ROOT" ] && BIN_DIR_ROOT=$DEFAULT_BIN_DIR_ROOT
    [ -z "$DATA_DIR_ROOT" ] && DATA_DIR_ROOT=$DEFAULT_DATA_DIR_ROOT
}

# install a patch package on All Hosts on which a package part of the package list is installed
# $1 is the patch package name
# $2 is the package list 
function install-patch-package {
    local _Package=$1
    local _RootPackageList=$2
    local _RootPackage
    local host
    local rootPackageHostFile
    local -A settings
    
    for _RootPackage in $_RootPackageList
    do
    # searches all host where the root package is installed and then installs the patch
        rootPackageHostFile=$INSTALLER_DATADIR/packages/$_RootPackage/inventory/hosts
        if [ -f $rootPackageHostFile ]
        then
            for host in `get-host-list $CONTEXT $rootPackageHostFile`
            do
                read-host-setting $CONTEXT $host $rootPackageHostFile settings
                if [ "${settings[InstallStatus]}" == "Installed" ] 
                then
                    BIN_DIR_ROOT=${settings[BIN_DIR_ROOT]}
                    DATA_DIR_ROOT=${settings[DATA_DIR_ROOT]}
                    if [ ! -z "${settings[ansible_ssh_user]}" ]
                    then
                        INSTALL_USER=${settings[ansible_ssh_user]}
                    fi
                    install-package $_Package $host
                fi
            done
        fi
    done
}

# check if the given argument is an alias name or not
# $1 is the package name
function isPackageAnAlias {
    local package=$1

    if [[ " ${Aliases[@]} " =~ .*\ $package[^\ ]*\ .* ]]
    then
        return 0
    else
        return 1
    fi
}
