# github-thrift-client-server

## Usage

Running the test server in the repl:

feeder.client> (feeder.testserve/start)
"INFO > Sun Feb 22 16:36:56 GMT 2015 > System started"
feeder.client> (server-healthy?)
"INFO > Sun Feb 22 16:36:58 GMT 2015 > Healthy!"
feeder.client> (feeder.testserve/stop)
nil
feeder.client> (server-healthy?)
"INFO > Sun Feb 22 16:37:12 GMT 2015 > Not Healthy!, message: java.net.ConnectException: Connection refused"
feeder.client> (feeder.testserve/start)
"INFO > Sun Feb 22 16:38:14 GMT 2015 > System started"
feeder.client> (count  (:pushes @feeder.testserve/push-db))
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
requesting: nil
finished pushing batch
nil
feeder.client> (count  (:pushes @feeder.testserve/push-db))
159

## License

Copyright © 2015 AL

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
