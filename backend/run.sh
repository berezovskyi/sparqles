#!/usr/bin/env bash
set -euo pipefail

CMDARGS="-p src/main/resources/sparqles_docker.properties"

## Manual task execution
# # grace period of 30s at startup before commencing the full cycle
# GRACE="${GRACE:=30}"
# # default loop runs every 5 minutes
# DELAY="${DELAY:=300}"

echo "Starting SPARQLes server"

# sleep $GRACE

# while :
# do
	echo "Running SPARQLes full cycle"
   # interop
	# echo "Running SPARQLes full cycle [ftask]"
	# bin/sparqles $CMDARGS -run ftask
   # # availability
	# echo "Running SPARQLes full cycle [atask]"
	# bin/sparqles $CMDARGS -run atask
   # # performance
	# echo "Running SPARQLes full cycle [ptask]"
	# bin/sparqles $CMDARGS -run ptask
   # index
	# echo "Running SPARQLes full cycle [itask]"
	# bin/sparqles $CMDARGS -run itask
	# # discoverability
   # echo "Running SPARQLes full cycle [dtask]"
	# bin/sparqles $CMDARGS -run dtask
   # # recompute
	# echo "Running SPARQLes full cycle [r]"
	# bin/sparqles $CMDARGS -r
   # # index view
	# echo "Running SPARQLes full cycle [iv]"
	# bin/sparqles $CMDARGS -iv
   # # stats
	# echo "Running SPARQLes full cycle [st]"
	# bin/sparqles $CMDARGS -st
# 	echo "SPARQLes full cycle FININSHED"
# 	sleep $DELAY
# done


## Fully automatic
bin/sparqles $CMDARGS --start

