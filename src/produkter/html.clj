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

(defn price-for-product [product-id prices prices-general]
  (let [price (first (filter #(= product-id (:product-id %)) prices))]
    (if (nil? price)
      (first (filter #(= product-id (:product-id %)) prices-general))
      price)))

(defn present-sortgroup [sg products prices]
  (let [productsg (get-sortgroup sg)
        prices-general (:prices (find-pricebook "YouSee"))]
    (html
      [:table
      (for [p productsg]
        (if (not (empty? (filter #(= (:id p) %) products)))
          [:tr
           [:td (radio-button sg false (:id p))]
           [:td (:name p)]
           [:td (:total-price (price-for-product (:id p) prices prices-general)) " kr/md"]
           [:td (:prov_system (meta p))]]))])))

(defn index []
  (layout "KONTRAKT" "En header"
    (form-to [:POST "/main"]
      (html
        [:table
          [:tr [:td (label :contractname "Kontrakt")] [:td (text-field :contractname "")]]])
      (submit-button "Next"))))

(defn main [contractname]
  (let [contract (find-contract contractname)]
    (let [sales-concept-name (:sales-concept-name contract)
          pricebook-name (:pricebook contract)]
      (let [prices (:prices (find-pricebook pricebook-name))
            products (:products (find-sales-concept sales-concept-name))]
        {:session {:products products :prices prices}
         :body (layout "MAIN" "En header"
          (form-to [:POST "/mandatory"]
            (present-sortgroup "tva" products prices) (present-sortgroup "bba" products prices)
            (submit-button "Next")))}))))

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

(defn productform [product]
  (html
    [:table
      [:tr [:td (label :id "Varenummer")] [:td (text-field :id (:id product))] [:td (label :name "Navn")] [:td (text-field :name (:name product))]]
      [:tr [:td (label :type "Produkt type")] [:td (drop-down :type *product-type* (:type product))] [:td (label :weight "Vaegt")] [:td (text-field :weight (:weight product))]]
      [:tr [:td (label :sortgroup "Sorterings gruppe")] [:td (text-field :sortgroup (:sortgroup product))] [:td (label :sort "Sortering")] [:td (text-field :sort (:sort product))]]
      [:tr [:td (label :bundle-products "Bundle produkter")] [:td [:select {:id "bundle-products" :name "bundle-products" :multiple "multiple" :size "7"} (select-options (get-products) (:bundle-products product))]] [:td (label :devoting-form "Afsaetnings form")] [:td (drop-down :devoting-form (get-devoting-forms) (:devoting-form product))]]
      ]))

(defn newproduct []
  (layout "New Product" "En header"
    (form-to [:POST "/viewproducts"]
      (hidden-field :create "true")
      (productform nil)
      (submit-button "Opret"))))

(defn editproduct [id]
  (let [p (if (not (nil? id)) (find-product id) nil)]
    (layout "Edit product" "En Header"
      (form-to [:POST "/viewproducts"]
        (hidden-field :update "true")
        (productform p)
        (submit-button "Opdater")))))

(defn viewproducts [req]
  (if (= "true" (get-in req [:params "create"]))
    (create-product (struct product (get-in req [:params "id"]) (get-in req [:params "name"]) (get-in req [:params "type"]) (get-in req [:params "weight"])
      (get-in req [:params "sortgroup"]) (get-in req [:params "sort"]) (get-in req [:params "bundle-products"]) (get-in req [:params "devoting-form"]))))
  (if (= "true" (get-in req [:params "update"]))
    (update-product (struct product (get-in req [:params "id"]) (get-in req [:params "name"]) (get-in req [:params "type"]) (get-in req [:params "weight"])
      (get-in req [:params "sortgroup"]) (get-in req [:params "sort"]) (get-in req [:params "bundle-products"]) (get-in req [:params "devoting-form"]))))
  (layout "Viev Products" "En header"
    (html
      [:table
       [:tr [:th "Varenummer"] [:th "Produkt"]]
      (for [p (get-products)]
        [:tr [:td (p 1)] [:td (p 0)] [:td [:a {:href "#" } "Editer"]] [:td [:a {:href "#" } "Tilf&oslash;j meta"]]])
    ])))

