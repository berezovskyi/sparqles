#!/usr/bin/env bash
set -euo pipefail

CMDARGS="-p src/main/resources/sparqles_docker.properties"

## Fully automatic
bin/sparqles $CMDARGS --start

## Manual task execution
# # grace period of 30s at startup before commencing the full cycle
# GRACE="${GRACE:=30}"
# # default loop runs every 5 minutes
# DELAY="${DELAY:=300}"

# echo "Starting SPARQLes server"

# sleep $GRACE

# while :
# do
# 	echo "Running SPARQLes full cycle"
# 	echo "Running SPARQLes full cycle [atask]"
# 	bin/sparqles $CMDARGS -run atask
# 	echo "Running SPARQLes full cycle [ptask]"
# 	bin/sparqles $CMDARGS -run ptask
# 	echo "Running SPARQLes full cycle [itask]"
# 	bin/sparqles $CMDARGS -run itask
# 	echo "Running SPARQLes full cycle [dtask]"
# 	bin/sparqles $CMDARGS -run dtask
# 	echo "Running SPARQLes full cycle [r]"
# 	bin/sparqles $CMDARGS -r
# 	echo "Running SPARQLes full cycle [iv]"
# 	bin/sparqles $CMDARGS -iv
# 	echo "SPARQLes full cycle FININSHED"
# 	sleep $DELAY
# done
