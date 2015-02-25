(defproject test-client "0.1.0-SNAPSHOT"
  :description "A dummy client for the thrift server"
  :url "https://github.com/plasma147/github-thrift-client-server"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]
                 [thrift-clj "0.2.1"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [api "0.2.0-SNAPSHOT"]]
  :plugins      [[lein-environ "1.0.0"]]
  :main ^:skip-aot test-client.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
