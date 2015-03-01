#!/bin/bash

exec 2>&1
exec > /var/log/web-interface.log
exec java -jar /opt/web-interface/web-interface-0.0.1-SNAPSHOT.jar

