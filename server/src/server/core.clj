(ns server.core
  (:gen-class)
  (:require [thrift-clj.core :as thrift]
            [environ.core :refer [env]]
            [server.events :as events]
            [server.service :refer [push-service]]))

;Stores the current running instance of the server. Allows for stopping and starting inside the repl.
(def system (atom nil))

(defn start 
  "Starts the server and sets running instance"
  [] (let [port  (Integer. (env :port 8080))
           host  (env :host "0.0.0.0")
           srv   (thrift/nonblocking-server 
                   push-service port
                   :bind        host 
                   :protocol :compact)]
    (reset! system (thrift/serve! srv))
    (events/info "System started, bind-host:" host ", port:" port )))

(defn stop 
  "Stops the running server and removes running instance"
  [] (if (nil? @system)
       (events/info "Server not running?")
       (do   
         (events/info "server stopping!")
         (thrift/stop! @system)
         (reset! system nil))))

(defn -main
  "starts the server"
  [& args] (start))
