#/bin/bash

NAME="thrift-server"
VERSION="0.1.0"
JAR_NAME="server-0.1.0-SNAPSHOT-standalone.jar"

DEB_NAME="${NAME}_${VERSION}_amd64.deb"
SRC_DIR="../target/uberjar/"
DEST_DIR="../target/dist/"
DEST_JAR_LOCATION="${DEST_DIR}/opt/${NAME}/"
DEST_SCRIPT_LOCATION="${DEST_DIR}/etc/service/${NAME}/"

echo "Running lein";
lein clean && lein uberjar;

mkdir -p ${DEST_JAR_LOCATION};
mkdir -p ${DEST_SCRIPT_LOCATION};

echo "Copying jar to ${DEST_JAR_LOCATION}";
cp -r $SRC_DIR/${JAR_NAME} ${DEST_JAR_LOCATION};

echo "Copying service script to ${DEST_SCRIPT_LOCATION}run";
cp -r run-service.sh ${DEST_SCRIPT_LOCATION}run;

echo "Creating rpm...";
fpm -s dir -t deb  -n $NAME -C $DEST_DIR -v $VERSION .;

dpkg -c $DEB_NAME;

echo "Moving rpm to ../target/";
mv $DEB_NAME ../target/;
