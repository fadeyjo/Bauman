#!/usr/bin/env python3
import sys

max_value = None
for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    key, val = line.split('\t', 1)
    try:
        val = float(val)
        if max_value is None or val > max_value:
            max_value = val
    except:
        continue

if max_value is not None:
    print(f"{max_value:.2f}")
