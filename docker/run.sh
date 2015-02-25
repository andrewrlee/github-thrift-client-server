#!/bin/bash

echo "Launching thriftserver container as \"thriftserver_01\""
sudo docker run --name thriftserver_01 -d -p 8080:8080 plasma147/thriftserver

echo "Launching mongodb container as \"mongodb_01\""
sudo docker run --name mongodb_01 -d -p 27017:27017 plasma147/mongodb --noprealloc --smallfiles
