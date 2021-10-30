#!/bin/bash

for i in $(seq 1 32); do
  cp extract_to_json_vbs.json extract_to_json_vbs$i.json
  # input path
  sed -i "s/\"path\": \"pathSource\"/\"path\": \"\/mnt\/hdd\/extractionV3C2\/video_split${i}\"" extract_to_json_vbs$i.json
  # path thumbnails
  sed -i "s/\"path\": \"pathThumbnails\/\"/\"path\": \"\/mnt\/hdd\/thumbnails${i}\"" extract_to_json_vbs$i.json
  # path output
  sed -i "s/\"host\": \"pathOutput\/\"/\"path\": \"\/mnt\/hdd\/output${i}\"" extract_to_json_vbs$i.json
done
