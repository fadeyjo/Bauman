#!/usr/bin/env python3
import sys
for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    city, creative_id, avg = line.split('\t')
    try:
        print(f"max\t{float(avg)}")
    except:
        continue
