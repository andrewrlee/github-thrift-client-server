(ns server.testserve
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [environ.core :refer [env]]))

(thrift/import
  (:types    [github.thrift.mongo.core.api Commit]
             [github.thrift.mongo.core.api Push])
  (:services github.thrift.mongo.core.api.PushService))

(defonce push-db (atom {:pushes []}))

(defn info
  "logs a series of statements"
  [& args] (println (str "INFO > " (java.util.Date.)  " > "  (apply str args))))

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
  [] (let [port  (Integer. (env :port 8080))
           host  (env :host "localhost")
           srv   (thrift/nonblocking-server 
                   push-service port
                   :bind        host 
                   :protocol :compact)]
    (reset! system (thrift/serve! srv))
    (info "System started, bind-host:" host ", port:" port )))

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
