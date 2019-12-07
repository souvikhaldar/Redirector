(ns web-dev-clojure.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.adapter.jetty :as jetty]))

; url is used as index and value is hashc {url hash}
(def store (ref {}))

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 40) 65))))))

; TODO: 
; Hash should be unique
; Some char are not valid for url
(defn insert-url! [url]
  (dosync
   (ref-set store (assoc @store url (rand-str 6)))))

(defn lookup-hash [hash]
  (some #(when (= (second %) hash) (first %)) @store))

(defn resp [body]
  {:headers {"Content-Type" "text/html"}
   :status 200
   :body body})

(defn shrink [request]
  (let [url (-> request :params :url)
        hash (get @store url)]
    (if (nil? hash)
      (resp (get (insert-url! url) url))
      (resp hash))))

(defn redirect [request]
  (let [hash (-> request :params :hash)
        url (lookup-hash hash)]
    (if url
      (resp url)
      (resp (str "invalid hash: " hash)))))

(defroutes redirector
  (GET "/shrink/:url" [] shrink)
  (GET "/redirect/:hash" [] redirect))

(defn -main []
  (jetty/run-jetty redirector {:port 3000}))