#!/bin/bash

exec 2>&1
exec > /var/log/thrift-server.log
exec java -jar /opt/thrift-server/server-0.1.0-SNAPSHOT-standalone.jar

