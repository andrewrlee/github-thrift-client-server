(ns test-client.core
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

(defn get-total-number-of-events
  [] (try 
       (with-open [c (connect)]
         (PushService/getTotalNumberOfPushes c))
       (catch Exception e (info "Not Healthy!, message: " (.getMessage e)))))

(defn query
  [term] (try 
       (with-open [c (connect)]
         (PushService/getPushes c term))
       (catch Exception e (info "Not Healthy!, message: " (.getMessage e)))))

(defn server-healthy? 
  "Pings the server and returns \"Healthy!\" if healthy or \"Not Healthy!\" and a message if otherwise"
  [] (try 
       (with-open [c (connect)]
         (let [response (PushService/ping c)
               healthy? (= response  "pong!")]
           (if healthy? (info "Healthy!") (info "Not Healthy!, response:" response))
           healthy?))
       (catch Exception e (info "Not Healthy!, message: " (.getMessage e)))))

(defn -main [& args] 
  (if (server-healthy?)
    (do  (println "There are " (get-total-number-of-events) " items")
         (doseq [term args] (println "searching for " term) (clojure.pprint/pprint (query  term))))))
