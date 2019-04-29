#!/bin/bash


my_global_var=1
function f1 {
    local my_local_var=1
    my_global_var=2
    my_new_global_var=1
    echo ">f1>my_global_var=$my_global_var"
    echo ">f1>my_local_var=$my_local_var"
    echo ">f1>my_new_global_var=$my_new_global_var"
}
echo "---------- before function called-----------"
echo ">my_global_var=$my_global_var"
echo ">my_new_global_var=$my_new_global_var"
echo ">my_local_var=$my_local_var"
echo "---------- while function called-----------"
f1
echo "---------- after function called-----------"
echo ">my_global_var=$my_global_var"
echo ">my_new_global_var=$my_new_global_var"
echo ">my_local_var=$my_local_var"
