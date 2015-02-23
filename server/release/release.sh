#/bin/bash

lein clean && lein uberjar;

fpm -s dir -t deb  -n thrift-server -C ../target/uberjar/ -v 0.1.0 --prefix /opt/thrift-server --deb-upstart thrift-server.conf ./server-0.1.0-SNAPSHOT-standalone.jar;

dpkg -c thrift-server_0.1.0_amd64.deb;

mv thrift-server_0.1.0_amd64.deb ../target/
