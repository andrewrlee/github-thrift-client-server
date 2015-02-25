(ns server.events
  (:gen-class)
  (:require [monger.core :as mg]
            [environ.core :refer [env]]
            [monger.collection :as mc]
            [monger.operators :refer [$regex]]
            [monger.conversion :refer [from-db-object]])
  (:import org.bson.types.ObjectId))

(def db-name "push-events")
(def collection "events")

(defn info
  "logs a series of statements"
  [& args] (println (str "INFO > " (java.util.Date.)  " > "  (apply str args))))

(defn get-total-number-of-events []
  (let [conn (mg/connect)
        db   (mg/get-db conn db-name)]
    (info "Querying number of events")
    (mc/count db collection)))

;db.events.find({'commits.message': {$regex: "count"}} )
(defn get-pushes [query]
  (let [conn (mg/connect)
        db   (mg/get-db conn db-name)]
    (info "Searching for:" query)
    (map #(from-db-object % true) (mc/find db collection {"commits.message" {$regex query}}))))
