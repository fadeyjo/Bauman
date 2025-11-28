#!/bin/bash

if [[ ${#} -ne 1 ]]; then
    echo "Пользователь не был указан"
    exit 1
fi

USER_DATA=$(cat /etc/passwd | grep "${1}")
IFS=':' read -r -a USER_DIR <<< "$USER_DATA"
echo "Домашняя дикертория пользователя ${1}:"
echo "$(ls -a /${USER_DIR[5]})"