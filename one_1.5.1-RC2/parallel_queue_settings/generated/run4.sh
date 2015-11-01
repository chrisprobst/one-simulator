#!/bin/sh
cd ..
./one.sh -b 30 parallel_queue_settings/generated/queue_mode_benchmark_settings_base.txt parallel_queue_settings/generated/par4/queue_mode_benchmark_settings_1.txt &
./one.sh -b 30 parallel_queue_settings/generated/queue_mode_benchmark_settings_base.txt parallel_queue_settings/generated/par4/queue_mode_benchmark_settings_2.txt &
./one.sh -b 30 parallel_queue_settings/generated/queue_mode_benchmark_settings_base.txt parallel_queue_settings/generated/par4/queue_mode_benchmark_settings_3.txt &
./one.sh -b 31 parallel_queue_settings/generated/queue_mode_benchmark_settings_base.txt parallel_queue_settings/generated/par4/queue_mode_benchmark_settings_4.txt &
fg
fg
fg
fg
cd parallel_queue_settings