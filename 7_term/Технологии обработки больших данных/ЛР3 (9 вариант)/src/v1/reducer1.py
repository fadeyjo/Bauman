#!/usr/bin/env python3
import sys

current_city = None
total = 0.0
count = 0

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    city, payment = line.split('\t', 1)
    payment = float(payment)
    if current_city is None:
        current_city = city
    if city != current_city:
        print(f"{current_city}\t{total / count:.2f}")
        current_city = city
        total = payment
        count = 1
    else:
        total += payment
        count += 1

if current_city:
    print(f"{current_city}\t{total / count:.2f}")
