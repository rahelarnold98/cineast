#!/bin/bash

for i in $(seq 1 32); do
  cp extract_OCR.json extraction_config/extract_OCR_$i.json
  # input path
  sed -i "s/\"path\": \"pathSource\"/\"path\": \"\/mnt\/hdd\/extractionV3C2\/video_split${i}\"/g" extraction_config/extract_OCR_$i.json
  # path thumbnails
  sed -i "s/\"destination\": \"pathThumbnails\/\"/\"destination\": \"\/mnt\/hdd\/thumbnails${i}\"/g" extraction_config/extract_OCR_$i.json
  # path output
  sed -i "s/\"host\": \"pathOutput\/\"/\"host\": \"\/mnt\/hdd\/outputSubtitle${i}\"/g" extraction_config/extract_OCR_$i.json
done
