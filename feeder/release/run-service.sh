#!/bin/bash

exec 2>&1
exec > /var/log/thrift-client.log
exec java -jar /opt/thrift-client/feeder-0.1.0-SNAPSHOT-standalone.jar

