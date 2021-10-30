#!/bin/bash

i=0
for f in /mnt/hdd/V3C2/videos/*; do
  d=video_split$(printf %d $((i / 305 + 1)))
  mkdir -p /mnt/hdd/extractionV3C2/$d
  cp -R "$f" /mnt/hdd/extractionV3C2/$d
  i=$((i + 1))
done
