#!/usr/bin/env python3
import sys

current_city = None
current_creative_id = None
total = 0.0
count = 0

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue
    city, creative_id, payment = line.split('\t', 2)
    creative_id = int(creative_id)
    payment = float(payment)
    if current_city is None:
        current_city = city
    if current_creative_id is None:
        current_creative_id = creative_id
    
    if city != current_city or creative_id != current_creative_id:
        print(f"{current_city}\t{current_creative_id}\t{total / count:.2f}")
        current_city = city
        current_creative_id = creative_id
        total = payment
        count = 1
    else:
        total += payment
        count += 1

if current_city:
    print(f"{current_city}\t{current_creative_id}\t{total / count:.2f}")
