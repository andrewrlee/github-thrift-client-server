(ns feeder.core
  (:gen-class)
  (:require [monger.core :as mg]
            [feeder.github :as github]
            [environ.core :refer [env]]
            [monger.collection :as mc]
            [schejulure.core :refer [schedule]])
  (:import org.bson.types.ObjectId))

(def db-name "push-events")
(def collection "events")

(defn import-events []
  (let [conn (mg/connect)
        db  (mg/get-db conn db-name)]
    (if (> (env :app-max-events 10000)  (mc/count db collection))
      (do  (github/info "Running import")
           (doseq [event  (github/push-events)]
             (mc/insert db collection (assoc event :_id (ObjectId.)))))
      (github/info "maximum events imported!, no more will be added"))))

(defn -main [& args]
  (let [every-minute {:minute (range 0 60 5)}]
    (schedule every-minute import-events)))
