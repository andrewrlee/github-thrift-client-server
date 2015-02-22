(ns feeder.testserve
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [feeder.github :as github]
            [feeder.client :refer [info]]))

(thrift/import
  (:types    [github.thrift.mongo.core.api Commit]
             [github.thrift.mongo.core.api Push])
  (:services github.thrift.mongo.core.api.PushService))

(defonce push-db (atom {:pushes []}))

;Definition of the service. ping returns "pong", addPush stores received pushes in an atom.
(thrift/defservice push-service
  PushService
  (ping [] 
    (info "pinged!")
    "pong!")
    
  (addPush [push]
    (info "Adding push for repo:" (:repo push))
    (swap! push-db update-in [:pushes] #(conj % push))
    (info "no " (count (:pushes @push-db))) 
    true))

;Stores the current running instance of the server. Allows for stopping and starting inside the repl.
(def system (atom nil))

(defn start 
  "Starts the server and sets running instance"
  [] (let [srv (thrift/nonblocking-server 
                 push-service 7009 
                 :bind "localhost" 
                 :protocol :compact)]
    (reset! system (thrift/serve! srv))
    (info "System started")))

(defn stop 
  "Stops the running server and removes running instance"
  [] (if (nil? @system)
       (println "Server not running?")
       (do   
         (info "server stopping!")
         (thrift/stop! @system)
         (reset! system nil))))

(defn get-stored-events-size [] (count (:pushes @push-db)))

(defn -main
  "starts the server"
  [& args] (start))
