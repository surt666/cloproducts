(ns routes
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.core       
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        ring.middleware.session
        ring.middleware.session.memory
        ring.util.response
        ring.util.servlet
        ring.handler.dump
        sandbar.stateful-session 
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
  (GET "/newsalesproduct" req
    (new-sales-product))
  (GET "/newdeliveryproduct" req
    (new-delivery-product))
  (GET "/editsalesproduct/:id" [id]
    (edit-sales-product id))
  (GET "/deletesalesproduct/:id" [id]
    (delete-sales-product id))
  (GET "/editdeliveryproduct/:id" [id]
    (edit-delivery-product id))
  (GET "/deletedeliveryproduct/:id" [id]
    (delete-delivery-product id))
  (GET "/addmeta/:id" [id]
    (addmeta id))
  (POST "/viewsalesproducts" req
    (view-sales-products req))
  (GET "/viewsalesproducts" req
    (view-sales-products req))
  (POST "/viewdeliveryproducts" req
    (view-delivery-products req))
  (GET "/viewdeliveryproducts" req
    (view-delivery-products req))
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
  (POST "/saveprice" [pricebook-id product-id general-price koda radio copydan digi discount start-date end-date]
    (saveprice pricebook-id product-id general-price koda radio copydan digi discount start-date end-date))
  (GET "/editprice/:pricebook/:productid" [pricebook productid type]
    (editprice pricebook productid type))
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
         ;(wrap-session (memory-store))
         (wrap-stateful-session)
         (wrap-stacktrace)))

(defservice app-routes)

(defn boot []
  (future (run-jetty #'app {:port 8080})))

(boot)