#!/bin/bash

for i in $(seq 1 32); do
  cp extract_to_json_vbs_p1.json extraction_config/extract_to_json_vbs_p1_$i.json
  # input path
  sed -i "s/\"path\": \"pathSource\"/\"path\": \"\/mnt\/hdd\/extractionV3C2\/video_split${i}\"/g" extraction_config/extract_to_json_vbs_p1_$i.json
  # path thumbnails
  sed -i "s/\"destination\": \"pathThumbnails\/\"/\"destination\": \"\/mnt\/hdd\/thumbnails${i}\"/g" extraction_config/extract_to_json_vbs_p1_$i.json
  # path output
  sed -i "s/\"host\": \"pathOutput\/\"/\"host\": \"\/mnt\/hdd\/output${i}\"/g" extraction_config/extract_to_json_vbs_p1_$i.json
done
