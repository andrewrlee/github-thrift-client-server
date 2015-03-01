#!/bin/bash

USAGE="Usage : $0 [build|start|stop]";

if [ $# -lt 1 ]
then
  echo $USAGE
  exit
fi

function start {
  sudo docker run --name mongodb_01 -d -p 27017:27017 plasma147/mongodb --noprealloc --smallfiles

  #these containers are linked to the mongodb_01 one. This adds lots of env variables and a host entry for "mongodb" -> <mongodb container hostname>
  sudo docker run --name thriftserver_01 -d -p 8080:8180                          --link mongodb_01:mongodb plasma147/thriftserver
  sudo docker run --name feeder_01       -d -e "APP_GITHUB_TOKEN=${GITHUB_TOKEN}" --link mongodb_01:mongodb plasma147/feeder
  sudo docker run --name webinterface_01 -d -p 8080:8080                          --link thriftserver_01:thriftserver plasma147/webinterface
}

function build-image {
  sudo docker build -f plasma147/basejava8/Dockerfile    --tag plasma147/basejava8 .
  sudo docker build -f plasma147/thriftserver/Dockerfile --tag plasma147/thriftserver .
  sudo docker build -f plasma147/feeder/Dockerfile       --tag plasma147/feeder .
  sudo docker build -f plasma147/mongodb/Dockerfile      --tag plasma147/mongodb .
  sudo docker build -f plasma147/webinterface/Dockerfile --tag plasma147/webinterface .
}

function stop {
  sudo docker stop  thriftserver_01 feeder_01 mongodb_01 webinterface_01
  sudo docker rm    thriftserver_01 feeder_01 mongodb_01 webinterface_01
}


case "$1" in

"build") build-image 
    ;;

"start") start 
    ;;

"stop")  stop
    ;;

*) echo $USAGE
   ;;
esac
