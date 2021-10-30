#!/bin/bash
a=24
for i in $(seq 1 32); do
  j=$((a+i))
  k=$(printf "%02d" $i)
  sed -i "${j}i\\ \\ \\ \\ \\ \\ - targets: ['localhost:48${k}']" docs/linux/prometheus.yml
done
