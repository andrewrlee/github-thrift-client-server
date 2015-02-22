(defproject api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [thrift-clj "0.2.1"]
                 [ch.qos.logback/logback-classic "1.0.13"]]
  :target-path "target/%s"
  :plugins [[lein-thriftc "0.2.1"]]
  :hooks [leiningen.thriftc]
  :thriftc {:path          "thrift"      
            :source-paths  ["src/thrift"] 
            :java-gen-opts "bean,hashcode"
            :force-compile false}   
  :profiles {:uberjar {:aot :all}})
