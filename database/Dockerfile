# Debian apt fail
# FROM mongo:2.6
# FROM mongo:3.0
# FROM mongo:3.1
# no entrypoint.sh
# FROM mongo:3.3
FROM mongo:3.2
# FROM mongo:3.4
# FROM mongo:3.7
# FROM mongo:4.0

COPY mongoDump.zip /tmp/

RUN cd /tmp/ && apt-get update && apt-get install -yq unzip && unzip mongoDump.zip && \
	mongod --fork --logpath /var/log/mongodb.log && \
	mongorestore -d sparqles /tmp/dump/sparqles && \
	mongod --shutdown

CMD docker-entrypoint.sh mongod