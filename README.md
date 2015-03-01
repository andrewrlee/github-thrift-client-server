# github-thrift-client-server

This repo consists of 4 related projects:
 - api     
 - feeder
 - server
 - web-interface 

The api project builds the library jar which contains classes generated by thrift. This is shared by both the thrift-server and the web-interface. The feeder application pulls information about "push" events from github and then pushes them into a mongodb instance. The thrift-server exposes an interface to allow the mongodb store to be queried. The web-interface is a presentation layer application written with [Spark](http://sparkjava.com), [Twitter Bootstrap](http://getbootstrap.com/) and [Ractive.js](http://www.ractivejs.org/) which uses this interface.  

The feeder traverses pages of events by calling the github api. This has been implemented as a lazy sequence of single events which transparently loads additional pages of events as required. 

####Service diagram:

![service diagram](https://docs.google.com/drawings/d/1zKKj4PVYpQ7R5aJqwsWMgEWNtw9_KOGamkyxqrI0q3E/pub?w=960&h=720 "Service diagram")

####Web interface:

![Frontend](https://docs.google.com/drawings/d/1aZ1LHbhza6NkKPTSBnTETqkLH9MzgYZ1dfqAPaEeoVw/pub?w=1207&h=614)

## Installation

### API

First, build the api project:

1.) Download [thrift](http://thrift.apache.org/) tarball:
   * `./configure` ensuring that java support is enabled (depends on java 1.7+ and ant).
   * `make && make install`

2.) Ensure the `thrift` executable is available on the path.

3.) run `lein jar` to generate the class files from thrift definitions.

4.) run `lein install` to install the library in the local maven repo.

This will make the library available to the thrift-server and web-interface.

### Thrift Server

To build the server jar:

```
cd server
lein uberjar
```

To run the server: `java -jar server-0.1.0-SNAPSHOT-standalone.jar`

This will launch a server that will respond to thrift requests by querying mongodb.

|ENV Variables:    | Defaults  |
|------------------|-----------| 
| APP_MONGO_HOST   | "mongodb" |
| APP_MONGO_PORT   | 27017     |
| APP_SERVER_PORT  | 8080      |
| APP_BIND_HOST    | "0.0.0.0" |

These can be overriden:

`APP_BIND_HOST=127.0.0.1 APP_SERVER_PORT=9000 java -jar server-0.1.0-SNAPSHOT-standalone.jar`

The .deb can be built by running `release.sh` script in the release directory.

### Feeder

To build the feeder jar 

```
cd feeder
lein uberjar
```

Get an api token from [github](https://github.com/blog/1509-personal-api-tokens).

To run the feeder: `GITHUB_TOKEN="<TOKEN>" java -jar feeder-0.1.0-SNAPSHOT-standalone.jar`

This will attempt to poll for latest events and insert them into mongo.

|ENV Variables:    | Defaults           |
|------------------|--------------------| 
| APP_MONGO_HOST   | "mongodb"          |
| APP_MONGO_PORT   | 27017              |
| APP_MAX_EVENTS   | 10000              |
| APP_GITHUB_TOKEN | &lt;no default&gt; |

These can be overriden:

`APP_MONGO_HOST=127.0.0.1 APP_MONGO_PORT=9000 APP_GITHUB_TOKEN=blah-blah-blah java -jar feeder-0.1.0-SNAPSHOT-standalone.jar`

The .deb can be built by running `release.sh` script in the release directory.

### Web Interface

To build the web jar

```
cd web-interface
mvn clean install
```
To run the web-interface: `java -jar web-interface-0.0.1-SNAPSHOT.jar`

This will serve the web frontend.

|ENV Variables:    | Defaults           |
|------------------|--------------------|
| APP_SERVER_PORT   | "8080"            |

These can be overriden:

`APP_SERVER_PORT=8222 java -jar web-interface-0.0.1-SNAPSHOT.jar`

##Run with Docker

This requires docker to be already installed. There a 4 containers, 1 for each of the following services:
 * mongodb
 * feeder
 * thrift-server 
 * web-interface

All of the containers are based on the [Ubuntu base image](http://phusion.github.io/baseimage-docker/) provided by phusion.
There is a basejava8 dockerfile that extends this for running java applications; the feeder, thrift-server and web-interface dockerfiles all extend this file. 
The application dockerfiles install the associated .deb file from the `dist` branch of this project. The .deb files are responsible for installing the jar and it's startup script (which will be run once the container is started).

There is a script for manipulating the docker containers: `docker/docker.sh`.

As a one off step, the images need to be built for each container:
```
  cd docker/
  sudo ./docker.sh build
```

After the images have been built, containers can be built and started with the following command:
```  
GITHUB_TOKEN="${TOKEN}" ./docker.sh start
```

The containers can safely be stopped and disposed of with the following command:
```
./docker.sh stop
```

It takes a bit of time for mongodb to start up once its container has run, meaning the feeder may fail the first time it is scheduled to run.

###TODO
* Use logback or some clj logging framework 
* Proper error handling
* Prevent duplicate events from being added to mongo
* Vagrant?
* Add commits to frontend/pagination
* Add serverside pagination

## License

Copyright © 2015 AL

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
