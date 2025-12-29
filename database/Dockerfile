# FROM mongo:3.4
# to upgrade to 3.6, you need to upgrade to 3.4 and run
# $ docker compose exec database-svc sh -c 'mongo'
# > db.adminCommand( { setFeatureCompatibilityVersion: "3.4" } )
FROM mongo:4.4.30

COPY mongoDump.tar.gz /tmp/
RUN cd /tmp/ && tar -xvzf mongoDump.tar.gz

RUN mongod --fork --logpath /var/log/mongodb.log && \
	mongorestore -d sparqles /tmp/dump/sparqles && \
	mongod --shutdown

# up to 3.2 incl
# ENTRYPOINT ["/entrypoint.sh"]
# after 3.2
ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["mongod"]
