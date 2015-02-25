#!/bin/bash

USAGE="Usage : $0 [build|start|kill]";

if [ $# -lt 1 ]
then
  echo $USAGE
  exit
fi

function start {
  echo "Launching thriftserver container as \"thriftserver_01\""
  sudo docker run --name thriftserver_01 -d -p 8080:8080 plasma147/thriftserver
  echo "Launching mongodb container as \"mongodb_01\""
  sudo docker run --name mongodb_01 -d -p 27017:27017 plasma147/mongodb --noprealloc --smallfiles
}

function build-image {
  sudo docker build -f plasma147/basejava8/Dockerfile --tag plasma147/basejava8 .
  sudo docker build -f plasma147/thriftserver/Dockerfile --tag plasma147/thriftserver .
  sudo docker build -f plasma147/mongodb/Dockerfile --tag plasma147/mongodb .
}

function kill {
  sudo docker rm -f thriftserver_01 mongodb_01
}


case "$1" in

"build") build-image 
    ;;

"start") start 
    ;;

"kill")  kill
    ;;

*) echo $USAGE
   ;;
esac
