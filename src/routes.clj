(ns routes
  (:use compojure.core       
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        ring.middleware.session
        ring.middleware.session.memory
        cloproducts.roles
        cloproducts.models
        hiccup.page-helpers
        hiccup.core
        hiccup.form-helpers)
  (:require [compojure.route :as route]))

(defn layout [title header & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:meta {:http-equiv "X-UA-Compatible" :content "IE=EmulateIE7"}]
        [:title (str title)]]
        ;(stylesheet "ideadb.css")]
      [:body
        [:div {:id "header"} header]
        [:div {:id "main"}
          body]
        [:div {:id "footer"}
          "Totalt fed Footer"
        ]]]))

(defn present-sortgroup [sg]
  (let [x (map #(:value %) (get-sortgroup sg))]
    (html
      [:table
      (for [n x]
        [:tr[:td (radio-button sg false (:id n))][:td (:name n)][:td (:price n) " kr/md"]])])))

(defn index []
  (layout "TEST" "En header"
    (form-to [:POST "/mandatory"]
      (present-sortgroup "tva") (present-sortgroup "bba")
      (submit-button "Next"))))

(defn mandatory [tva bba]
  (let [order (struct order {} {:tva tva :bba bba})])
  (layout "TEST2" "En header"
    (html
      [:h2 (str tva "," bba)])
      (link-to "/sess" "sess")))

(defn sess [req]
  (layout "SESS" "En header"
    (println "REQ" req)
    (html
      [:h2 (str ((req :session) :tva))])))

(defroutes test-routes
  (GET "/" req (index))
;  (POST "/mandatory" req
;    (mandatory (get-in req [:params "bba"]) (get-in req [:params "tva"])))
  (POST "/mandatory" [tva bba]
    (mandatory tva bba))
  (GET "/sess" req (sess req))
  (route/not-found "Page not found"))

(def app1
     (-> (var test-routes)
         (wrap-reload '(routes))
         (wrap-reload '(cloproducts.roles))
         (wrap-session (memory-store))
         (wrap-stacktrace)))


(defn boot []
  (run-jetty #'app1 {:port 8080}))

(defn create-products []
  (create-product (struct product 1101001 "Grundpakke" "tv" 1 "tva" 1 149.00 nil))
  (create-product (struct product 1101032 "Mellempakke" "tv" 2 "tva" 2 199.00 nil))
  (create-product (struct product 1101003 "Fuldpakke" "tv" 3 "tva" 3 279.00 nil))
  (create-product (struct product 1301001 "Bredbaand 4/1 Mbit/s" "bb" 1 "bba" 1 129.00 nil))
  (create-product (struct product 1301002 "Bredbaand 12/1 Mbit/s" "bb" 2 "bba" 2 149.00 nil))
  (create-product (struct product 1301003 "Bredbaand 20/2 Mbit/s" "bb" 3 "bba" 3 189.00 nil))
  (create-product (struct product 1301004 "Bredbaand 50/3 Mbit/s" "bb" 4 "bba" 4 249.00 nil)))

(defn create-products-mandatory []
  (create-product (struct product 1121001 "Oprettelse" "tv" 1 "tvs" 1 2049.00 nil))
  (create-product (struct product 1321001 "Standard Goer det selv" "bb" 1 "bbs" 1 129.00 nil))
  (create-product (struct product 1321002 "Traadloes Goer det selv" "bb" 2 "bbs" 2 149.00 nil))
  (create-product (struct product 1321003 "Standard Tekniker" "bb" 1 "bbt" 3 189.00 nil))
  (create-product (struct product 1321004 "Traadloes Tekniker" "bb" 2 "bbt" 4 249.00 nil)))
