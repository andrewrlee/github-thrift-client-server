(ns feeder.client
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [environ.core :refer [env]]))

(thrift/import
  (:types   [github.thrift.mongo.core.api Commit]
            [github.thrift.mongo.core.api Push])
  (:clients github.thrift.mongo.core.api.PushService))

(defn info 
  "logs a series of statements"
  [& args] (println (str "INFO > " (java.util.Date.)  " > "  (apply str args))))

(defn- connect [] 
  (let [host-port [ (env :host "localhost")  (env :port 8080)]]
    (info "connecting on " host-port)
    (thrift/connect! PushService (thrift/framed host-port :protocol :compact))))

(defn server-healthy? 
  "Pings the server and returns \"Healthy!\" if healthy or \"Not Healthy!\" and a message if otherwise"
  [] (try 
       (with-open [c (connect)]
         (let [response (PushService/ping c)]
           (if (= response  "pong!") (info "Healthy!") (info "Not Healthy!, response:" response))))
       (catch Exception e (info "Not Healthy!, message: " (.getMessage e)))))

(defn post-recent-push-events 
  "Post all recent push events to the server. This may include duplicates, if less than 300 events have occurred since last being run"
  [] (with-open [c (connect)]
       (doseq [item (events-seq)] (PushService/addPush c item))
       (println "finished pushing batch")))

(defn -main [& args] 
  (server-healthy?)
  (post-recent-push-events))
