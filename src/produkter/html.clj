(ns produkter.html
  (:use produkter.roles
        produkter.models
        repositories.couch-repository
        hiccup.page-helpers
        hiccup.core
        hiccup.form-helpers))

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
  (let [x (get-sortgroup sg)]
    (html
      [:table
      (for [n x]
        [:tr[:td (radio-button sg false (:id n))][:td (:name n)][:td (:price n) " kr/md"][:td (:prov_system (meta n))]])])))

(defn index []
  (layout "MAIN" "En header"
    (form-to [:POST "/mandatory"]
      (present-sortgroup "tva") (present-sortgroup "bba")
      (submit-button "Next"))))

(defn mandatory [tva bba]
  {:session {:order (struct order {} {:tva tva :bba bba})}
   :body (layout "MANDATORY" "En header"
    (form-to [:POST "/user-info"]
      (if (not (= tva nil))
        (present-sortgroup "tvs"))
      (if (not (= bba nil))
        (html (present-sortgroup "bbs") (present-sortgroup "bbt")))
      (submit-button "Next")))})

(defn user-info [req]
  (println "PO" (((req :session) :order) :products))
  (let [prods (((req :session) :order) :products)
        cust (((req :session) :order) :customer)]
    (println "P1" prods "|" (get-in req [:params "tvs"]))
    (let [products (assoc prods :tvs (get-in req [:params "tvs"]) :bbs (get-in req [:params "bbs"]))]
    (println "P2" products)
    {:session {:order (struct order cust products)}
     :body (layout "USER" "En header"
       (form-to [:POST "/invoice"]
       [:table
         [:tr
           [:td (label :firstname "Fornavn") (text-field :firstname "")]
           [:td (label :lastname "Efternavn") (text-field :lastname "")]]
         [:tr
           [:td (label :street "Vej") (text-field :street "")]
           [:td (label :number "Nr.") (text-field :number "")]]
         [:tr
           [:td (label :floor "Etage") (text-field :floor "")]
           [:td (label :side "Side") (text-field :side "")]]
         [:tr
           [:td (label :zip "Post Nr.") (text-field :zip "")]
           [:td (label :city "by") (text-field :city "")]]]
         (submit-button "Next")))})))

(defn invoice [req]
  (println "POI" (((req :session) :order) :products))
  (layout "INVOICE" "En header"
    (html [:h2 (str (req :session) :order)])))

