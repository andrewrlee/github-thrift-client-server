(defproject feeder "0.1.0-SNAPSHOT"
  :description "Polls github event endpoint and persists events to mongo"
  :url "https://github.com/plasma147/github-thrift-client-server"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]
                 [clj-http "1.0.1"]
                 [cheshire "5.4.0"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [com.novemberain/monger "2.1.0"]
                 [schejulure "1.0.1"]]
  :plugins      [[lein-environ "1.0.0"]]
  :main ^:skip-aot feeder.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
