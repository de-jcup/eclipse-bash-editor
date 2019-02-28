#!/bin/bash

varIncluded1="I am included!"
echo "hello from included..."

function doSomething(){
    varLocal1="I am local in doSomething() with param:$1"
    echo "i do something and have a local variable inside:$varLocal1"
}
