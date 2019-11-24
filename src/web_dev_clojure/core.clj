(ns web-dev-clojure.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.adapter.jetty :as jetty]))

(def url-map {})
(def reverse-map {})

;; random string generator
(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 40) 65))))))


;; Shrink the provided URL
(defn shrink [request]
  (def incoming_url (-> request :params :url))
  (def final_url "")
  {
    :status 200
    :headers {"Content-Type" "text/html"}
   :body (if (contains? reverse-map incoming_url)
           (do (def final_url (get reverse-map incoming_url))
                (str "Already shrunken URL is "  final_url))
                (do (def new_url (rand-str 5))
                    (def final_url (str "redirector.io/" new_url))
                    (def url-map (assoc url-map final_url incoming_url))
                    (def reverse-map (assoc reverse-map incoming_url final_url))
                    (str "The new shrunken URL is " final_url)))
  })



;; Return the original URL corresponding to the provided
;; shrunken URL
;; TODO Redirect instead of return
(defn redirect [request]
  (def url (str "redirector.io/" (-> request :params :url)))
  {:status 200
   :body (if (contains? url-map url)
           (str "The original URL is: " (get url-map url))
           (str "shrunken URL not found, try to shorten it first."))
   :headers {"Content-Type" "text/html"}})

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defroutes redirector
  (GET "/shrink/:url" [] shrink)
  (GET "/redirect/redirector.io/:url" [] redirect))

(defn -main []
  (jetty/run-jetty redirector {:port 3000}))
