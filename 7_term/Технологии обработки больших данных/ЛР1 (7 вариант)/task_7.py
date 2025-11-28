#!/usr/bin/env python3

import sys
import argparse
from hdfs import InsecureClient

def walk(client, path):
    try:
        entries = client.list(path, status=True)
    except Exception as e:
        print(f"ERROR: cannot access {path}: {e}")
        return
    for name, status in entries:
        full = path.rstrip('/') + '/' + name
        typ = status.get('type', '')  # 'FILE' or 'DIRECTORY'
        if typ == 'DIRECTORY':
            print(f"DIR : {full}")
            walk(client, full)
        else:
            size = status.get('length', '?')
            print(f"FILE: {full} (size={size})")

def main():
    parser = argparse.ArgumentParser(description='Recursively list HDFS paths.')
    parser.add_argument('--namenode', '-n', default='http://localhost:9870',
                        help='URL of NameNode WebHDFS endpoint, e.g. http://master:9870')
    parser.add_argument('--user', '-u', default='hduser', help='HDFS user to connect as')
    parser.add_argument('paths', nargs='+', help='HDFS path(s) to walk, e.g. /user/hduser/Hadoop')
    args = parser.parse_args()

    client = InsecureClient(args.namenode, user=args.user)

    for p in args.paths:
        print(f"Walking {p} ...")
        walk(client, p)

if __name__ == '__main__':
    main()
