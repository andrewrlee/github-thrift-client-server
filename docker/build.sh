#!/bin/bash

sudo docker build -f plasma147/basejava8/Dockerfile --tag plasma147/basejava8 .
sudo docker build -f plasma147/thriftserver/Dockerfile --tag plasma147/thriftserver .
sudo docker build -f plasma147/mongodb/Dockerfile --tag plasma147/mongodb .
