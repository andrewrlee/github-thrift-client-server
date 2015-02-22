(ns feeder.client
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [feeder.github :as github]))

(thrift/import
  (:types   [github.thrift.mongo.core.api Commit]
            [github.thrift.mongo.core.api Push])
  (:clients github.thrift.mongo.core.api.PushService))

(defn info 
  "logs a series of statements"
  [& args] (println (str "INFO > " (java.util.Date.)  " > "  (apply str args))))

(defn- commit->thrift 
  "Converts commit edn hash -> Commit record -> thrift object"
  [item]
  (let [convert-commit (comp thrift/->thrift map->Commit)]
    (update-in item [:commits] (fn [col] (vec (map convert-commit col))))))

;Converts push edn hash -> Push record -> thrift object 
(def clj->thrift 
  (comp thrift/->thrift 
        map->Push 
        commit->thrift))

(defn- events-seq 
  "Maps the event-seq (which transparantly pages the event feed) to a seq of thrift objects"
  [] (map clj->thrift (github/push-events)))

(defn server-healthy? 
  "Pings the server and returns \"Healthy!\" if healthy or \"Not Healthy!\" and a message if otherwise"
  [] (try 
       (with-open [c (thrift/connect! PushService (thrift/framed ["localhost" 7009]) :protocol :compact)]
         (let [response (PushService/ping c)]
           (if (= response  "pong!") (info "Healthy!") (info "Not Healthy!, response:" response))))
       (catch Exception e (info "Not Healthy!, message: " (.getMessage e)))))

(defn post-recent-push-events 
  "Post all recent push events to the server. This may include duplicates, if less than 300 events have occurred since last being run"
  [] (with-open [c (thrift/connect! PushService (thrift/framed ["localhost" 7009]) :protocol :compact)]
       (doseq [item (events-seq)] (PushService/addPush c item))
       (println "finished pushing batch")))
