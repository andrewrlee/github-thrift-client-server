#!/bin/bash

exec 2>&1
exec > /var/log/feeder.log
exec java -jar /opt/feeder/feeder-0.1.0-SNAPSHOT-standalone.jar

