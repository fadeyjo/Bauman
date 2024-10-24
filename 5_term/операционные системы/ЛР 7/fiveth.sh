#!/bin/sh

TEMP_DIR="/home/fadeyjo/"

CURRENT_MONTH=$(date +"%Y-%m")
ONE_WEEK_AGO=$(date -d '7 days ago' +%s)

find "$TEMP_DIR" -type f -newermt "$CURRENT_MONTH-01" ! -newermt "$CURRENT_MONTH-31" | while read -r file; do
    last_modified=$(stat -c %Y "$file")
    
    if [[ $last_modified -le $ONE_WEEK_AGO ]]; then
        if grep -q "test" "$file"; then
            sed -i 's/test/tset/g' "$file"
            echo "Modified: $file"
        fi
    fi
done
