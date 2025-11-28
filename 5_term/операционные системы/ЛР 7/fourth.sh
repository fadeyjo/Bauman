#!/bin/sh

if [[ ${#} -eq 1 ]]; then
    echo "Необходимое количество аргументов: 1"
    exit 1
fi
ps --ppid $1