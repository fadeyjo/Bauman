#!/bin/sh

decimal_to_roman() {
    local num=$1
    local roman=""
    local values=(1000 900 500 400 100 90 50 40 10 9 5 4 1)
    local symbols=("M" "CM" "D" "CD" "C" "XC" "L" "XL" "X" "IX" "V" "IV" "I")

    for i in "${!values[@]}"; do
        while [ $num -ge ${values[$i]} ]; do
            roman+="${symbols[$i]}"
            num=$((num - ${values[$i]}))
        done
    done
    echo "$roman"
}

if [ $# -lt 1 ]; then
    echo "Использование: $0 <число> [число ...]"
    exit 1
fi

output_file="output.txt"
> "$output_file"

for number in "$@"; do
    if ! [[ "$number" =~ ^[0-9]+$ ]]; then
        echo "Ошибка: '$number' не является допустимым десятичным числом."
        echo "Ошибка: '$number' не является допустимым десятичным числом." >> "$output_file"
        continue
    fi

    if [ "$number" -gt 3999 ]; then
        echo "Ошибка: '$number' некорректно, максимальное 3999."
        echo "Ошибка: '$number' некорректно, максимальное 3999." >> "$output_file"
        continue
    fi
    
    roman=$(decimal_to_roman "$number")
    echo "$number = $roman"
    echo "$number = $roman" >> "$output_file"
done

echo "Результат сохранен в $output_file"

