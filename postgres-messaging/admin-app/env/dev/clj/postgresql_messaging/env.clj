(ns admin-app.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [admin-app.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[admin-app started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[admin-app has shut down successfully]=-"))
   :middleware wrap-dev})
