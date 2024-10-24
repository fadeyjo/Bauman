#!/bin/sh

DIR="/var/log"


for file in "$DIR"/*; do
    if [[ $(stat --format="%s" ${file}) -ge 51200 ]]; then
        echo $file
    fi
done