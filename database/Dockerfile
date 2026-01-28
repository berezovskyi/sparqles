# FROM mongo:3.4
# to upgrade to 3.6, you need to upgrade to 3.4 and run
# $ docker compose exec database-svc sh -c 'mongo'
# > db.adminCommand( { setFeatureCompatibilityVersion: "3.4" } )
FROM mongodb/mongodb-community-server:7.0.29-ubuntu2204

USER root
RUN mkdir -p /var/log/mongodb && chown mongodb:mongodb /var/log/mongodb

USER mongodb
COPY --chown=mongodb:mongodb mongoDump.tar.gz /tmp/
RUN cd /tmp/ && tar -xvzf mongoDump.tar.gz

RUN mongod --fork --logpath /var/log/mongodb/mongod.log --bind_ip_all && \
	mongorestore -d sparqles /tmp/dump/sparqles && \
	mongod --shutdown

# The base image uses python3 /usr/local/bin/docker-entrypoint.py
# We can just rely on the base image entrypoint or specify it explicitly if needed.
# ENTRYPOINT ["python3", "/usr/local/bin/docker-entrypoint.py"]
CMD ["mongod"]
