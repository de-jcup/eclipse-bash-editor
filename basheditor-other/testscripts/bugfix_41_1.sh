#!/bin/bash

declare -A hostSettings

function display-installation {

    IStatus=${hostSettings[InstallStatus]}
    CStatus=${hostSettings[ConfigStatus]}

    
    #display parameters
    package_Idx=${PackageIdxMap[`basename $package`]}
    display-param-from-file $package/var/${InputFileMap[$package_Idx]} "$context:$host"
}

function display-param-from-file {
}