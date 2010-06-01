(ns produkter.html
  (:use produkter.roles
        produkter.models
        ring.util.response
        repositories.couch-repository
        hiccup.page-helpers
        hiccup.core
        hiccup.form-helpers
        clojure.contrib.seq-utils))

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
         [:div {:class="wrapper"} [:ul {:class="hnav dotted"} [:li [:a {:href "http://yousee.dk/Nyheder/Overblik.aspx"} "Nyheder"]] [:li [:a {:href "http://yousee.dk/Om_YouSee/Oversigt.aspx"} "Om YouSee"]]
                                   [:li [:a {:href "http://yousee.dk/Kontakt.aspx"} "Find butik"]] [:li {:class="last"} [:a {:href "http://yousee.dk/Kontakt/Kontakt.aspx"} "Kontakt os"]]]]

        ]]]))

(defelem select-options-multiple
  "Creates a seq of option tags from a collection and an optional coll of selected vals. Based on Hiccup select-options"
  ([coll] (select-options-multiple coll nil))
  ([coll selected-coll]
    (for [x coll]
      (if (sequential? x)
        (let [[text val] x]          
          [:option {:value val :selected (not (empty? (filter #(= val %) selected-coll)))} text])
        [:option {:selected (not (empty? (filter #(= val %) selected-coll)))} x]))))

(defn header []
  (html
    [:table {:border "1"}
     [:tr
      [:td [:a {:href "/viewproducts"} "Se Produkter"]] [:td [:a {:href "/"} "Bestil"]]
      [:td [:a {:href "/newproduct"} "Opret produkt"]] [:td [:a {:href "/viewpricebooks"} "Se prisb&oslash;ger"]]]]))

(defn price-for-product [product-id prices prices-general]
  "Try and find price in contract specific pricebook. If not there find it in general pricebook.
   filter is supposed to be lazy so we don't need to break out of the loop"
  (let [price (first (filter #(= product-id (:product_id %)) prices))]
    (if (nil? price)
      (first (filter #(= product-id (:product_id %)) prices-general))
      price)))

(defn present-sortgroup [sg products prices prices-general]
  "Get products belonging to the sortgroup, for the products available in the contract.
  Takes a list of contract specific prices, and the general prices, so as not to calculate these again and again"
  (let [productsg (get-sortgroup sg)]
    (html
      [:table
      (for [p productsg]        
        (if (not (empty? (filter #(= (:id p) %) products)))
          [:tr
           [:td (radio-button sg false (:id p))]
           [:td (:name p)]
           [:td (:total_price (price-for-product (:id p) prices prices-general)) " kr/md"]
           [:td (:prov_system (meta p))]]))])))

(defn index []
  (layout "KONTRAKT" (header)
    (form-to [:post "/main"]
      (html
        [:table
          [:tr [:td (label :contractname "Kontrakt")] [:td (text-field :contractname "")]]])
      (submit-button "Next"))))

(defn main [contractname]
  (let [contract (find-contract contractname)]
    (let [sales-concept-name (:sales_concept_name contract)
          pricebook-name (:pricebook contract)]
      (let [prices (:prices (find-pricebook pricebook-name))
            prices-general (:prices (find-pricebook "YouSee"))
            products (:products (find-sales-concept sales-concept-name))]        
        {:session {:products products :prices prices :prices-general prices-general}
         :body (layout "MAIN" (header)
          (form-to [:POST "/mandatory"]
            (html (present-sortgroup "tva" products prices prices-general) (present-sortgroup "bba" products prices prices-general) (present-sortgroup "mobb" products prices prices-general))
            (submit-button "Next")))}))))

(defn mandatory [req]
  (let [prices ((req :session) :prices)
        prices-general ((req :session) :prices-general)
        products ((req :session) :products)
        tva (get-in req [:params "tva"])
        bba (get-in req [:params "bba"])]
  {:session {:order (struct order {} {:tva tva :bba bba})}
   :body (layout "MANDATORY" (header)
    (form-to [:post "/user-info"]
      (if (not (= tva nil))
        (present-sortgroup "tvs" products prices prices-general))
      (if (not (= bba nil))
        (html (present-sortgroup "bbs" products prices prices-general) (present-sortgroup "bbt" products prices prices-general)))
      (submit-button "Next")))}))

(defn user-info [req]
  (let [order ((req :session) :order)]
    {:session {:order (assoc order :products (conj (:products order) {:tvs (get-in req [:params "tvs"]) :bbs (get-in req [:params "bbs"]) :bbt (get-in req [:params "bbt"])}))}
     :body (layout "USER" (header)
       (form-to [:post "/invoice"]
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
         (submit-button "Next")))}))

(defn invoice [req]
  (let [order ((req :session) :order)]
    (let [updated-order (assoc order :customer {:firstname (get-in req [:params "firstname"]) :lastname (get-in req [:params "lastname"])})]
      {:session {:order updated-order}
       :body (layout "INVOICE" (header)
        (html [:h2 (str updated-order)]))})))

(defn productform [product]
  (html
    [:table
      [:tr [:td (label :id "Varenummer")] [:td (text-field :id (:id product))] [:td (label :name "Navn")] [:td (text-field :name (:name product))]]
      [:tr [:td (label :type "Produkt type")] [:td (drop-down :type *product-type* (:type product))] [:td (label :weight "V&aelig;gt")] [:td (text-field :weight (:weight product))]]
      [:tr [:td (label :sortgroup "Sorterings gruppe")] [:td (text-field :sortgroup (:sortgroup product))] [:td (label :sort "Sortering")] [:td (text-field :sort (:sort product))]]
      [:tr [:td (label :bundle-products "Bundle produkter")] [:td [:select {:id "bundle-products" :name "bundle-products" :multiple "multiple" :size "7"} (select-options-multiple (get-products) (:bundle_products product))]] [:td (label :devoting-form "Afs&aelig;tnings form")] [:td (drop-down :devoting-form (get-devoting-forms) (:devoting_form product))]]
      ]))

(defn newproduct []
  (layout "New Product" (header)
    (form-to [:post "/viewproducts"]
      (hidden-field :create "true")
      (productform nil)
      (submit-button "Opret"))))

(defn editproduct [id]
  (let [p (if (not (nil? id)) (find-product id) nil)]
    (layout "Edit product" (header)
      (form-to [:post "/viewproducts"]
        (hidden-field :update "true")        
        (productform p)
        (submit-button "Opdater")))))

(defn viewproducts [req]
  (if (= "true" (get-in req [:params "create"]))
    (create-product (struct product (Integer/parseInt (get-in req [:params "id"])) (get-in req [:params "name"]) (get-in req [:params "type"]) (get-in req [:params "weight"])
      (get-in req [:params "sortgroup"]) (get-in req [:params "sort"]) (map #(Integer/parseInt %) (get-in req [:params "bundle-products"])) (get-in req [:params "devoting-form"]))))
  (if (= "true" (get-in req [:params "update"]))
    (update-product (assoc (find-product (Integer/parseInt (get-in req [:params "id"]))) :name (get-in req [:params "name"]) :type (get-in req [:params "type"]) :weight (get-in req [:params "weight"])
      :sortgroup (get-in req [:params "sortgroup"]) :sort (get-in req [:params "sort"]) :bundle_products (map #(Integer/parseInt %) (get-in req [:params "bundle-products"]))
      :devoting_form (get-in req [:params "devoting-form"]))))
  (layout "Viev Products" (header)
    (html
      [:table
       [:tr [:th "Varenummer"] [:th "Produkt"]]
      (for [p (get-products)]
        [:tr [:td (p 1)] [:td (p 0)] [:td [:a {:href (str "/editproduct/" (p 1))} "Editer"]] [:td [:a {:href (str "/viewmeta/" (p 1))} "Se meta"]]])
    ])))

(defn addmeta [id]
  (let [p (find-product id)]
    (layout "Add Meta" (header)
      (form-to [:post "/savemeta"]
        (hidden-field :metaupdate "true")
        (hidden-field :id id)
        [:table
          [:tr [:td (drop-down :metakey *property-keys* nil)] [:td (text-field :metaval nil)]]]
        (submit-button "Opdater")))))

(defn savemeta [req]
  (let [id (get-in req [:params "id"])]
    (if (= "true" (get-in req [:params "metaupdate"]))
      (let [p (find-product id)]
        (update-product (with-meta p (conj {(get-in req [:params "metakey"]) (get-in req [:params "metaval"])} (meta p))))))
    (redirect (str "/viewmeta/" id))))

(defn viewmeta [id]
  (let [p (find-product id)]
    (layout "Se Meta" (header)
      (html
        [:table
         [:tr [:th "Key"] [:th "Value"]]
         (for [key (keys (meta p))]
          [:tr [:td key] [:td ((meta p) key)]])]
        [:a {:href (str "/addmeta/" id)} "Tilf&oslash;j meta"][:a {:href "/viewproducts"} "Vis produkter"]))))

(defn viewpricebooks []
  (let [pricebooks (get-pricebooks)]
    (layout "Prisb&oslash;ger" (header)
      (html
        [:table
         [:tr [:th "Prisbog"]]
         (for [p pricebooks]
           [:tr [:td p]
                [:td [:a {:href (str "/addproducttopricebook/" p)} "Tilf&oslash;j produkt"]]
                [:td [:a {:href (str "/showproductsinpricebook/" p)} "Vis produkt priser"]]])]))))

(defn calculate-vat [general-price]
  (* 0.25 general-price))

(defn saveprice [pricebook-id product-id type general-price koda radio copydan digi discount]
  (let [pricebook (find-pricebook pricebook-id)]
    (let [prices (:prices pricebook)]
      (let [prices-edited (filter #(not (and (= (Integer/parseInt product-id) (:product_id %)) (= type (:type %)))) prices)]
      (update-pricebook (assoc pricebook :prices (conj prices-edited (struct price (Integer/parseInt product-id) type (Double/parseDouble general-price)
        (calculate-vat (Double/parseDouble general-price)) (Double/parseDouble koda) (Double/parseDouble radio) (Double/parseDouble copydan) (Double/parseDouble digi) (Double/parseDouble discount)
        (- (+ (Double/parseDouble general-price) (calculate-vat (Double/parseDouble general-price)) (Double/parseDouble koda) (Double/parseDouble radio)
          (Double/parseDouble copydan) (Double/parseDouble digi)) (Double/parseDouble discount)))))))))
  (redirect (str "/showproductsinpricebook/" pricebook-id)))

(defn priceform [price]
  (html
    [:table
        [:tr [:td (label :product-id "Varenummer")] [:td (drop-down :product-id (get-products) (:product_id price))]]
        [:tr [:td (label :type "Pris type")] [:td (drop-down :type *price-types* (:type price))]]
        [:tr [:td (label :general-price "Pris")] [:td (text-field :general-price (if (not (nil? price)) (:general_price price) 0))]]
        [:tr [:td (label :koda "Koda")] [:td (text-field :koda (if (not (nil? price)) (:koda price) 0))]]
        [:tr [:td (label :radio "RadiKoda")] [:td (text-field :radio (if (not (nil? price)) (:radio price) 0))]]
        [:tr [:td (label :copydan "CopyDan")] [:td (text-field :copydan (if (not (nil? price)) (:copydan price) 0))]]
        [:tr [:td (label :digi "Digital Rettigheder")] [:td (text-field :digi (if (not (nil? price)) (:digi price) 0))]
        [:tr [:td (label :discount "Rabat")] [:td (text-field :discount (if (not (nil? price)) (:discount price) 0))]]]]))

(defn add-product-to-pricebook [id]
  (layout (str "Tilf&oslash;j produkt til prisbog " id) (header)
    (form-to [:post "/saveprice"]
      (hidden-field :pricebook-id id)
      [:h2 (str "Tilf&oslash;j produkt til prisbog " id)]
      (priceform nil)
      (submit-button "Tilf&oslash;j pris"))))

(defn show-products-in-pricebook [id]
  (let [pricebook (find-pricebook id)]
    (layout (str "Vis produkter i prisbog " id) (header)
      [:table
        [:tr [:th "Varenummer"] [:th "Pris type"] [:th "Pris"] [:th "Koda"] [:th "RadiKoda"] [:th "CopyDan"] [:th "Digital Rettigheder"] [:th "Rabat"] [:th "Moms"] [:th "Total Pris"]]
        (for [price (:prices pricebook)]
          [:tr [:td (:product_id price)] [:td (:type price)] [:td (:general_price price)] [:td (:koda price)] [:td (:radio price)]
            [:td (:copydan price)] [:td (:digi price)] [:td (:discount price)] [:td (:vat price)] [:td (:total_price price)]
            [:td [:a {:href (str "/editprice/" id "/" (:product_id price) "/" (:type price))} "Opdater pris"]]
            [:td [:a {:href (str "/deleteprice/" id "/" (:product_id price))} "Slet pris"]]])])))

(defn editprice [pricebook-id product-id type]
  (layout (str "Ret produktpris i prisbog " pricebook-id) (header)
    (form-to [:post "/saveprice"]
      (hidden-field :pricebook-id pricebook-id)
      [:h2 (str "Ret produktpris i prisbog " pricebook-id " " type)]
      (priceform (first (filter #(and (= (Integer/parseInt product-id) (:product_id %)) (= type (:type %))) (:prices (find-pricebook pricebook-id)))))
      (submit-button "Ret pris"))))

(defn deleteprice [pricebook-id product-id]
  (let [pricebook (find-pricebook pricebook-id)]
    (let [prices (:prices pricebook)]
      (let [prices-edited (filter #(not (= (Integer/parseInt product-id) (:product_id %))) prices)]
        (update-pricebook (assoc pricebook :prices prices-edited)))))
  (redirect (str "/showproductsinpricebook/" pricebook-id)))