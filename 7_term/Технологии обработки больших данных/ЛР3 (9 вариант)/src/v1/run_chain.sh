#!/bin/bash
set -e

hdfs dfs -rm -r -f /user/hduser/mapreduce_chain/output1 /user/hduser/mapreduce_chain/output2 || true

STREAMING_JAR=$(ls $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming*.jar | head -n1)

echo "=== MR1: Средняя стоимость по городам России ==="
hadoop jar "$STREAMING_JAR" \
  -files mapper1.py,reducer1.py \
  -input /user/hduser/mapreduce_chain/input \
  -output /user/hduser/mapreduce_chain/output1 \
  -mapper "python3 mapper1.py" \
  -reducer "python3 reducer1.py"

echo "=== MR2: Максимальная средняя стоимость ==="
hadoop jar "$STREAMING_JAR" \
  -files mapper2.py,reducer2.py \
  -input /user/hduser/mapreduce_chain/output1/part-00000 \
  -output /user/hduser/mapreduce_chain/output2 \
  -mapper "python3 mapper2.py" \
  -reducer "python3 reducer2.py"

echo "=== Выгрузка результатов ==="
mkdir -p ~/mapreduce_chain/first_task ~/mapreduce_chain/second_task
hdfs dfs -get -f /user/hduser/mapreduce_chain/output1/part-00000 ~/mapreduce_chain/first_task/city_avg.txt
hdfs dfs -get -f /user/hduser/mapreduce_chain/output2/part-00000 ~/mapreduce_chain/second_task/max_payment.txt
