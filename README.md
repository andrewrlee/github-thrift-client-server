# github-thrift-client-server

This project currently consists of 2 clojure projects:

 - api     
 - feeder

The api project builds the library jar which contains classes generated by thrift. This is shared by both the client and the server. The feeder application pulls information about "push" events from github and then sends them via thrift to a server. At the moment there's only a test server implementation in feeder/testserve.clj. This test server just stores the received push events in memory.  

The feed traversal has been implemented as a lazy sequence of single events which transparently loads additional pages of events as required. 

## Installation

First, build the api project:

1.) Download [thrift](http://thrift.apache.org/) tarball:
   * `./configure` ensuring that java support is enabled (depends on java 1.7+ and ant).
   * `make && make install`

2.) Ensure the `thrift` executable is available on the path.

3.) run `lein jar` to generate the class files from thrift definitions.

4.) run `lein install` to install the library in the local maven repo.

This will make the library available to other projects.

Then get an api token from [github](https://github.com/blog/1509-personal-api-tokens) and add an env variable to `~/.lein/profiles.clj`:

```clojure
{:user
  {:env { :github-token   "<api-token>"}}}
```

and then fire up the repl: `lein repl` 

## Usage

Running the test server in the repl:

```clojure
feeder.client> (feeder.testserve/start)
INFO > Sun Feb 22 16:36:56 GMT 2015 > System started
feeder.client> (server-healthy?)
INFO > Sun Feb 22 16:36:58 GMT 2015 > Healthy!
feeder.client> (feeder.testserve/stop)
INFO > Sun Feb 22 16:37:04 GMT 2015 > server stopping!
feeder.client> (server-healthy?)
INFO > Sun Feb 22 16:37:12 GMT 2015 > Not Healthy!, message: java.net.ConnectException: Connection refused
feeder.client> (feeder.testserve/start)
INFO > Sun Feb 22 16:38:14 GMT 2015 > System started
feeder.client> (feeder.testserve/get-stored-events-size)
0
feeder.client> (post-recent-push-events)
requesting: https://api.github.com/events
requesting: https://api.github.com/events?page=2
requesting: https://api.github.com/events?page=3
requesting: https://api.github.com/events?page=4
requesting: https://api.github.com/events?page=5
requesting: https://api.github.com/events?page=6
requesting: https://api.github.com/events?page=7
requesting: https://api.github.com/events?page=8
requesting: https://api.github.com/events?page=9
requesting: https://api.github.com/events?page=10
finished pushing batch
feeder.client> (feeder.testserve/get-stored-events-size)
159
```
##TODO

* Implement daemon style functionality in the feeder - polling at a set interval.
* Use logback or some clj logging framework 
* Proper error handling
* Make client configurable (host/port)
* Add server written in a different language.
* Better namespace names
* Tests!

## License

Copyright © 2015 AL

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
