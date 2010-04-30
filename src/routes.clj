(ns routes
  (:use compojure.core       
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        ring.middleware.session
        ring.middleware.session.memory
        ring.handler.dump
        cloproducts.html)
  (:require [compojure.route :as route]))


(defroutes app-routes
  (GET "/" req (index))
;  (POST "/mandatory" req
;    (mandatory (get-in req [:params "bba"]) (get-in req [:params "tva"])))
  (POST "/mandatory" [tva bba]
    (mandatory tva bba))
  (POST "/user-info" req
    (user-info req))
  (POST "/invoice" req
    (invoice req))
  (GET "/set-session"  [] {:body "set session" :session {:a-key "a value"}})
  (GET "/read-session" {s :session} {:body (str "session: " s)})
  (GET "/dump-request" r 
(ring.handler.dump/handle-dump r))
  (route/not-found "Page not found"))

(def app
     (-> (var app-routes)
         (wrap-reload '(cloproducts.html))
         (wrap-reload '(cloproducts.roles))
         (wrap-session (memory-store))
         (wrap-stacktrace)))


(defn boot []
  (run-jetty #'app {:port 8080}))

(boot)