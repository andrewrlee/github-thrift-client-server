(ns server.service
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [environ.core :refer [env]]
            [server.events :as events]))

(thrift/import
  (:types    [github.thrift.mongo.core.api Commit]
             [github.thrift.mongo.core.api Push])
  (:services github.thrift.mongo.core.api.PushService))

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

(thrift/defservice push-service
  PushService
  (ping [] 
    (events/info "ping called")
    "pong!")
  
  (getTotalNumberOfPushes [] 
    (events/info "gettingTotalNumberOfPushes called")
    (events/get-total-number-of-events))
  
  (getPushes [query]
    (events/info "getPushes called:" query)
    (into #{} (map clj->thrift (events/get-pushes query)))))
