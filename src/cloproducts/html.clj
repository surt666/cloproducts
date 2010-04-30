(ns cloproducts.html
  (:use cloproducts.roles
        cloproducts.models
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
  (let [x (map #(:value %) (get-sortgroup sg))]
    (html
      [:table
      (for [n x]
        [:tr[:td (radio-button sg false (:id n))][:td (:name n)][:td (:price n) " kr/md"]])])))

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
        (label :firstname "Fornavn") (text-field :firstname "")
        (label :lastname "Efternavn") (text-field :lastname "")
        [:br]
        (label :street "Vej") (text-field :street "")
        (label :number "Nr.") (text-field :number "")
        [:br]
        (label :floor "Etage") (text-field :floor "")
        (label :side "Side") (text-field :side "")
        [:br]
        (label :zip "Post Nr.") (text-field :zip "")
        (label :city "by") (text-field :city "")
        [:br]
        (submit-button "Next")))})))

(defn invoice [req]
  (println "POI" (((req :session) :order) :products))
  (layout "INVOICE" "En header"
    (html [:h2 (str (req :session) :order)])))

