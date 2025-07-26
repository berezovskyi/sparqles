#!/usr/bin/env bash
set -euo pipefail

CMDARGS="-p src/main/resources/sparqles_docker.properties"

## Manual task execution
# # grace period of 30s at startup before commencing the full cycle
# GRACE="${GRACE:=30}"
# # default loop runs every 5 minutes
# DELAY="${DELAY:=300}"

echo "Starting SPARQLes server"
export JAVA_OPTS="-XX:MaxRAMPercentage=80 -javaagent:/usr/local/sparqles/opentelemetry-javaagent.jar"
# sleep $GRACE

# while :
# do

   #echo "Running SPARQLes full cycle"
   # interop
	#echo "Running SPARQLes full cycle [ftask]"
	#bin/sparqles $CMDARGS -run ftask
   # # availability
	#echo "Running SPARQLes full cycle [atask]"
	#bin/sparqles $CMDARGS -run atask
   # coherence
	#echo "Running SPARQLes full cycle [ctask]"
	#bin/sparqles $CMDARGS -run ctask
   # # performance
	#echo "Running SPARQLes full cycle [ptask]"
	#bin/sparqles $CMDARGS -run ptask
	# # discoverability
   #echo "Running SPARQLes full cycle [dtask]"
	#bin/sparqles $CMDARGS -run dtask
   # stats
	#echo "Running SPARQLes full cycle [st]"
	#bin/sparqles $CMDARGS -st

   # # recompute
	#echo "Running SPARQLes full cycle [r]"
	#bin/sparqles $CMDARGS -r
	#echo "Running SPARQLes - recompute last [rl]"
	#bin/sparqles $CMDARGS -rl
	# index view
	# FIXME: crashes on SPARQLES.recomputeIndexView
   #echo "Running SPARQLes full cycle [iv]"
	#bin/sparqles $CMDARGS -iv

   # index from old.datahub.io
	# echo "Running SPARQLes full cycle [itask]"
	# bin/sparqles $CMDARGS -run itask

# 	echo "SPARQLes full cycle FININSHED"
# 	sleep $DELAY
# done

#echo "Running SPARQLes [reschedule all tasks]"
#bin/sparqles $CMDARGS -run reschedule

#echo "${JAVA_OPTS}"

echo "Running SPARQLes [start service]"
## Fully automatic
JAVA_OPTS="${JAVA_OPTS} " bin/sparqles $CMDARGS --start

