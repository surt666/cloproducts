(ns routes
  (:use compojure.core       
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        ring.middleware.session
        ring.middleware.session.memory
        ring.util.response
        ring.handler.dump
        produkter.html)
  (:require [compojure.route :as route]))


(defroutes app-routes
  (GET "/" req (index))
;  (POST "/mandatory" req
;    (mandatory (get-in req [:params "bba"]) (get-in req [:params "tva"])))
  (POST "/main" [contractname]
    (main contractname))
  (POST "/mandatory" req
    (mandatory req))
  (POST "/user-info" req
    (user-info req))
  (POST "/invoice" req
    (invoice req))
  (GET "/newproduct" req
    (newproduct))
  (GET "/editproduct/:id" [id]
    (editproduct id))
  (GET "/addmeta/:id" [id]
    (addmeta id))
  (POST "/viewproducts" req
    (viewproducts req))
  (GET "/viewproducts" req
    (viewproducts req))
  (GET "/viewmeta/:id" [id]
    (viewmeta id))
  (POST "/savemeta" req
    (savemeta req))
  (GET "/viewpricebooks" req
    (viewpricebooks))
  (GET "/addproducttopricebook/:id" [id]
    (add-product-to-pricebook id))
  (GET "/showproductsinpricebook/:id" [id]
    (show-products-in-pricebook id))
  (POST "/saveprice" [pricebook-id product-id general-price koda radio copydan digi discount]
    (saveprice pricebook-id product-id general-price koda radio copydan digi discount))
  (GET "/editprice/:pricebook/:productid" [pricebook productid]
    (editprice pricebook productid))
  (GET "/deleteprice/:pricebook/:productid" [pricebook productid]
    (deleteprice pricebook productid))
  (GET "/set-session"  [] {:body "set session" :session {:a-key "a value"}})
  (GET "/read-session" {s :session} {:body (str "session: " s)})
  (GET "/dump-request" r 
  (ring.handler.dump/handle-dump r))
    (route/not-found "Page not found"))

(def app
     (-> (var app-routes)
         (wrap-reload '(produkter.html))
         (wrap-reload '(produkter.roles))
         (wrap-session (memory-store))
         (wrap-stacktrace)))


(defn boot []
  (run-jetty #'app {:port 8080}))

(boot)