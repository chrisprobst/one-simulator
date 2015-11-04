java -Xmx1500M -cp ./bin;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* 2> .\\reports\\actions.json.tmp
python merge_action_log.py
del .\\reports\\actions.json.tmp