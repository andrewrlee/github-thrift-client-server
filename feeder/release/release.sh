#/bin/bash

NAME="thrift-client"
VERSION="0.1.0"
LOCATION="/opt/$NAME"
JAR_NAME="feeder-0.1.0-SNAPSHOT-standalone.jar"
DEB_NAME="${NAME}_${VERSION}_amd64.deb"

lein clean && lein uberjar;

fpm -s dir -t deb  -n $NAME -C ../target/uberjar/ -v $VERSION --prefix $LOCATION --deb-upstart "${NAME}.conf" "./${JAR_NAME}";

dpkg -c $DEB_NAME;

mv $DEB_NAME ../target/
