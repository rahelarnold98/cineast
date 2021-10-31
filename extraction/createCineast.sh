#!/bin/bash

for i in $(seq 1 32); do
  cp ../cineast_to_json.json ../cineast_config/cineast$i.json
  j=$(printf "%02d" $i)
  # httpPort
  sed -i "s/4567/46${j}/g" ../cineast_config/cineast$i.json
  # grpcPort
  sed -i "s/4570/47${j}/g" ../cineast_config/cineast$i.json
  # prometheusPort
  sed -i "s/4569/48${j}/g" ../cineast_config/cineast$i.json
  # enablePrometheus
  sed -i "s/\"enablePrometheus\": false,/\"enablePrometheus\": true,/g" ../cineast_config/cineast$i.json
  # changeObjectLocation
  sed -i "s/\"objectLocation\": \".\/v3c1-mini\",/\"objectLocation\": \"\/mnt\/hdd\/extractionV3C2\/video_split${i}\",/g" ../cineast_config/cineast$i.json
  # changeVitrivrngLocation
  sed -i "s/\"uiLocation\": \"..\/vitrivr-ng\/dist\",/\"uiLocation\": \"\/home\/ubuntu\/vitrivr-ng\",/g" ../cineast_config/cineast$i.json

done
