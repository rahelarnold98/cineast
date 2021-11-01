#!/bin/bash
# create session
tmux new-session -d -s extraction
for i in $(seq 1 32); do
  # create windows
  tmux send-keys -t extraction "tmux new-window -n split${i} " ENTER
done

for i in $(seq 1 32); do
  # start cineast
  tmux send-keys -t extraction "tmux send-keys -t split${i} 'java -jar cineast-api/build/libs/cineast-api-3.4.0-SNAPSHOT-all.jar cineast_config/cineast${i}.json' ENTER" ENTER
done

# wait to be sure that cineast started
sleep 20s


for i in $(seq 1 32); do
  # start extraction
  tmux send-keys -t extraction:split${i} "extract --extraction extraction_config/extract_to_json_vbs_p1_${i}.json" C-m
done

