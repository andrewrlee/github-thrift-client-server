#!/bin/bash

exec 2>&1
exec > /var/log/test-client.log
exec java -jar /opt/test-client/test-client-0.1.0-SNAPSHOT-standalone.jar

