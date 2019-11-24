(ns web-dev-clojure.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.adapter.jetty :as jetty]))
(def port ":3000")
(def host "localhost")
(def url-map {})
(def reverse-map {})

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 40) 65))))))

(defn shorten [request]
  (def incoming-url (-> request :params :url))
  (if (contains? reverse-map incoming-url)
    (do {:status 200
   :body (str "Already Shrunken URL is " (get reverse-map incoming-url))
   :headers {"Content-Type" "text/html"}})
    (do (def new_url (rand-str 5))
        (def url-map (assoc url-map new_url incoming-url))
        (def reverse-map (assoc reverse-map incoming-url new_url))
        {:status 200
     :body (str "The new shrunken URL is " new_url)
     :headers {"Conten-Type" "text/html"}})))

(defn redirect [request]
  (def url (-> request :params :url))
  {:status 200
   :body (if (contains? url-map url)
           (str "The original URL is: " (get url-map url))
           (str "shrunken URL not found, try to shorten it first."))
   :headers {"Content-Type" "text/html"}})

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defroutes redirector
  (GET "/shorten/:url" [] shorten)
  (GET "/redirect/:url" [] redirect))

(defn -main []
  (jetty/run-jetty redirector {:port 3000}))
