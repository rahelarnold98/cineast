#!/bin/bash
# create session
tmux new-session -d -s extraction
for i in $(seq 1 3); do
  # create windows
  tmux send-keys -t extraction "tmux new-window -n split${i} " ENTER
done

for i in $(seq 1 3); do
  # start cineast
  tmux send-keys -t extraction "tmux send-keys -t split${i} 'java -jar ../cineast-api/build/libs/cineast-api-3.0.3-SNAPSHOT-all.jar ../cineast_config/cineast${i}.json' ENTER" ENTER
done

# wait to be sure that cineast started
sleep 20s

for i in $(seq 1 3); do
  # start extraction
  tmux send-keys -t extraction "tmux send-keys -t split${i} 'extract --extraction ../extraction_config/extract_to_json_job_${i}.json' ENTER" ENTER
done

