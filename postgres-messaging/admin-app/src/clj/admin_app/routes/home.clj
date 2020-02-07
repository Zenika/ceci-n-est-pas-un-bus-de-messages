(ns admin-app.routes.home
  (:require
   [admin-app.layout :as layout]
   [admin-app.db.core :as db]
   [clojure.java.io :as io]
   [admin-app.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn messages-page [request]
  (let [messages (db/get-messages)]
    (layout/render request "messages.html" {:messages messages})))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/messages" {:get messages-page}]
   ["/about" {:get about-page}]])

