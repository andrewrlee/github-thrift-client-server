(defproject feeder "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]
                 [clj-http "1.0.1"]
                 [cheshire "5.4.0"]
                 [thrift-clj "0.2.1"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [api "0.1.0-SNAPSHOT"]]
  :plugins      [[lein-environ "1.0.0"]]
  :main ^:skip-aot feeder.client
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})