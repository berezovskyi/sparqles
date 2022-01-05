# Debian apt fail
# FROM mongo:2.6
FROM mongo:3.2
# FROM mongo:3.4
# FROM mongo:3.7
# FROM mongo:4.0

# apt-get needs Mongo 3.2+ image as of 2022-01 due to Debian EOL
# COPY mongoDump.zip /tmp/
# RUN apt-get update && apt-get install -yq unzip && cd /tmp/ && unzip mongoDump.zip

COPY mongoDump.tar.gz /tmp/
RUN cd /tmp/ && tar -xvzf mongoDump.tar.gz

RUN mongod --fork --logpath /var/log/mongodb.log && \
	mongorestore -d sparqles /tmp/dump/sparqles && \
	mongod --shutdown

# up to 3.2 incl
ENTRYPOINT ["/entrypoint.sh"]
# after 3.2
# ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["mongod"]
