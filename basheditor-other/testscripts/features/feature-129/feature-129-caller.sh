#!/usr/bin/env bash

function fooX(){
    echo "i am local not from external..."
}
fooX localPart
foo1 fromLib1
foo2 fromLib2
