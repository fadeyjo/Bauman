#!/usr/bin/env python3
import sys
for line in sys.stdin:
    line = line.strip()
    if line.startswith("userId"):
        continue
    parts = line.split(',')
    if len(parts) < 6:
        continue
    userId, country, city, campaign_id, creative_id, payment = parts
    if country.strip().lower() == 'russia':
        try:
            print(f"{city.strip()}\t{int(creative_id)}\t{float(payment)}")
        except:
            continue
