(ns routes
  (:use compojure.core       
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        ring.middleware.session
        ring.middleware.session.memory
        cloproducts.html)
  (:require [compojure.route :as route]))


(defroutes app-routes
  (GET "/" req (index))
;  (POST "/mandatory" req
;    (mandatory (get-in req [:params "bba"]) (get-in req [:params "tva"])))
  (POST "/mandatory" [tva bba]
    (mandatory tva bba))
  (GET "/sess" req (sess req))
  (route/not-found "Page not found"))

(def app
     (-> (var app-routes)
         (wrap-reload '(routes))
         (wrap-reload '(cloproducts.roles))
         (wrap-session (memory-store))
         (wrap-stacktrace)))


(defn boot []
  (run-jetty #'app {:port 8080}))