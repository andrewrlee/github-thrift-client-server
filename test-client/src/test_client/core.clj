(ns feeder.core
  (:gen-class)
  (:require [monger.core :as mg]
            [feeder.github :as github]
            [environ.core :refer [env]]
            [monger.collection :as mc]
            [schejulure.core :refer [schedule]])
  (:import org.bson.types.ObjectId))

(defn import-events []
  (let [conn (mg/connect)
        db  (mg/get-db conn "monger-test")]
    (github/info "Running import")
    (doseq [event  (github/push-events)]
      (mc/insert db "events" (assoc event :_id (ObjectId.))))))

(defn -main [& args]
  (let [every-minute {:minute (range 0 60 1)}]
    (schedule every-minute import-events)))
