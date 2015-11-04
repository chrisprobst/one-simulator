#! /bin/sh
java -Xmx512M -cp ./bin:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
