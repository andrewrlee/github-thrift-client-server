(ns feeder.github
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [environ.core :refer [env]]))

(def token (env :github-token))
(def api-root-url "https://api.github.com/events")

(defn- make-request [url]
  (if url 
    (do (println "requesting:" url)
        (let [response (client/get url {:basic-auth ["x-oauth-basic" token]})]
           {:body (parse-string (:body response) true) :next-link (get-in response [:links :next :href])}))))

(defn- date->long [s] 
  (let [format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'hh:mm:ss")]
    (.getTime (.parse format s))))

(defn- ->event [item]
  (letfn [(to-commit [commit] {:sha (:sha commit) :message (:message commit) :user (get-in commit [:author :name])})]
    {:user        (get-in item [:actor :login])
     :repo        (get-in item [:repo  :url])
     :occurredat (date->long (:created_at item))
     :commits     (vec (map to-commit (get-in item [:payload :commits])))}))

(defn- as-push-event-seq [response] 
  (->> response
       (filter #(= "PushEvent" (:type %)))
       (map ->event)))

(defn- event-seq
  ([]  
    (event-seq api-root-url) ) 
  ([url]
   (if-let [response (make-request url)] 
     (lazy-cat (:body response) (lazy-seq (event-seq (:next-link response)))))))

(defn push-events [] (as-push-event-seq (event-seq)))
