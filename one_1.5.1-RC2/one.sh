#! /bin/sh
java -Xmx1500M -cp ./bin:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $* 2> ./reports/actions.json.tmp
./merge_action_log.py
rm ./reports/actions.json.tmp