#! /bin/sh
rm -rf reports/*
./one.sh -b 1 prophetV2_settings.txt
mv reports/prophetV2_scenario_MessageStatsReport.txt reports/1
./one.sh -b 1 prophetV2_settings.txt
mv reports/prophetV2_scenario_MessageStatsReport.txt reports/2
diff reports/1 reports/2