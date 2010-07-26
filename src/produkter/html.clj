(ns produkter.html
  (:use produkter.roles
        produkter.models
        ring.util.response
        repositories.couch-repository
        hiccup.page-helpers
        hiccup.core
        hiccup.form-helpers
        clj-time.format
        clj-time.core
        clj-time.coerce
        sandbar.stateful-session
        clojure.contrib.seq-utils))

(def custom-formatter (formatter "dd-MM-yyyy"))

(defn layout [title header & body]
  (html
    (doctype :html4)
    (xhtml-tag "da"
      [:head        
        [:title (str title)]]
        ;(stylesheet "ideadb.css")]
      [:body
        [:div {:id "header"} header]
        [:div {:id "main"}
          body]
        [:div {:id "footer"}
         [:div {:class="wrapper"} [:ul {:class="hnav dotted"} [:li [:a {:href "http://yousee.dk/Nyheder/Overblik.aspx"} "Nyheder"]] [:li [:a {:href "http://yousee.dk/Om_YouSee/Oversigt.aspx"} "Om YouSee"]]
                                   [:li [:a {:href "http://yousee.dk/Kontakt.aspx"} "Find butik"]] [:li {:class="last"} [:a {:href "http://yousee.dk/Kontakt/Kontakt.aspx"} "Kontakt os"]]]

        ]]])))

(defelem select-options-multiple
  "Creates a seq of option tags from a collection and an optional coll of selected vals. Based on Hiccup select-options"
  ([coll] (select-options-multiple coll nil))
  ([coll selected-coll]
    (for [x coll]
      (if (sequential? x)
        (let [[text val] x]          
          [:option {:value val :selected (not (empty? (filter #(= val %) selected-coll)))} text])
        [:option {:selected (not (empty? (filter #(= val %) selected-coll)))} x]))))

(defn html-header []
  (html
    [:table 
     [:tr
      [:td [:a {:href "/viewsalesproducts"} "Salgs Produkter"]] [:td "|"] [:td [:a {:href "/viewdeliveryproducts"} "Leverings Produkter"]] [:td "|"] [:td [:a {:href "/"} "Bestil"]] [:td "|"]
      [:td [:a {:href "/newsalesproduct"} "Opret Salgs Produkt"]] [:td "|"] [:td [:a {:href "/newdeliveryproduct"} "Opret Leverings Produkt"]] [:td "|"] [:td [:a {:href "/viewpricebooks"} "Prisb&oslash;ger"]]]]))

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
  ;(println (get-sortgroup sg))
  (let [productsg (sort-by #(vec (map % [:sortgroup :sort])) (get-sortgroup sg))]
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
  (layout "KONTRAKT" (html-header)
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
        (session-put! :prices prices)
        (session-put! :prices-general prices-general)
        (session-put! :products products)
        (layout "MAIN" (html-header)
          (form-to [:POST "/mandatory"]
            (html (present-sortgroup "tva" products prices prices-general) (present-sortgroup "bba" products prices prices-general) (present-sortgroup "mobb" products prices prices-general))
            (submit-button "Next")))))))

(defn mandatory [req]
  (let [prices (session-get :prices)
        prices-general (session-get :prices-general)
        products (session-get :products)
        tva (get-in req [:params "tva"])
        bba (get-in req [:params "bba"])]
  (session-put! :order (struct order {} {:tva tva :bba bba}))
  (layout "MANDATORY" (html-header)
    (form-to [:post "/user-info"]
      (if (not (= tva nil))
        (present-sortgroup "tvs" products prices prices-general))
      (if (not (= bba nil))
        (html (present-sortgroup "bbm" products prices prices-general)))
      (submit-button "Next")))))

(defn user-info [req]
  (let [order (session-get :order)]
    (session-put! :order (assoc order :products (conj (:products order) {:tvs (get-in req [:params "tvs"]) :bbm (get-in req [:params "bbm"])})))
    (layout "USER" (html-header)
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
        (submit-button "Next")))))

(defn invoice [req]
  (let [order (session-get :order)]
    (let [updated-order (assoc order :customer {:firstname (get-in req [:params "firstname"]) :lastname (get-in req [:params "lastname"])})]
      (session-put! :order updated-order)
      (layout "INVOICE" (html-header)
        (html [:h2 (str updated-order)])))))

(defn productform [product]
  (html
    [:table
      [:tr [:td (label :id "Varenummer")] [:td (text-field :id (:id product))] [:td (label :name "Navn")] [:td (text-field :name (:name product))]]
      [:tr [:td (label :sales-type "Salgs type")] [:td (drop-down :sales-type *sales-types* (:type product))] [:td (label :weight "V&aelig;gt")] [:td (text-field :weight (:weight product))]]
      [:tr [:td (label :sortgroup "Sorterings gruppe")] [:td (text-field :sortgroup (:sortgroup product))] [:td (label :sort "Sortering")] [:td (text-field :sort (:sort product))]]
      [:tr [:td (label :delivery-products "Leverings produkter")] [:td [:select {:id "delivery-products" :name "delivery-products" :multiple "multiple" :size "7"} (select-options-multiple (get-delivery-products) (:delivery_products product))]] [:td (label :binding-period "Bindings periode")] [:td (drop-down :binding-period *binding-periods* (:binding_period product))]]
      [:tr [:td (label :start "Start")] [:td (text-field :start (if (not (nil? (:start (:sales_channels product)))) (:start (:sales_channels product)) (unparse custom-formatter (now))))] [:td (label :end "Slut")] [:td (text-field :end (:end (:sales_channels product)))]]
      [:tr [:td (label :portal-start "Portal start")] [:td (text-field :portal-start (if (not (nil? (:portal_start (:sales_channels product)))) (:portal_start (:sales_channels product)) (unparse custom-formatter (now))))] [:td (label :portal-end "Portal slut")] [:td (text-field :portal-end (:portal_end (:sales_channels product)))]]
      [:tr [:td (label :dealer-start "Forhandler start")] [:td (text-field :dealer-start (if (not (nil? (:dealer_start (:sales_channels product)))) (:dealer_start (:sales_channels product)) (unparse custom-formatter (now))))] [:td (label :dealer-end "Forhandler slut")] [:td (text-field :dealer-end (:dealer_end (:sales_channels product)))]]
      [:tr [:td (label :spoc-start "SPOC start")] [:td (text-field :spoc-start (if (not (nil? (:spoc_start (:sales_channels product)))) (:spoc_start (:sales_channels product)) (unparse custom-formatter (now))))] [:td (label :spoc-end "SPOC slut")] [:td (text-field :spoc-end (:spoc_end (:sales_channels product)))]]
        ]))

(defn new-sales-product []
  (layout "New Product" (html-header)
    (form-to [:post "/viewsalesproducts"]
      (hidden-field :create "true")
      (productform nil)
      (submit-button "Opret"))))

(defn new-delivery-product []
  (layout "New Product" (html-header)
    (form-to [:post "/viewdeliveryproducts"]
      (hidden-field :create "true")
      [:table
          [:tr [:td (label :name "Navn")] [:td (text-field :name nil)] [:td (label :delivery-type "Produkt type")] [:td (drop-down :delivery-type *delivery-type* nil)]]]
      (submit-button "Opret"))))

(defn edit-sales-product [id]
  (let [p (if (not (nil? id)) (find-product id) nil)]
    (layout "Edit product" (html-header)
      (form-to [:post "/viewsalesproducts"]
        (hidden-field :update "true")        
        (productform p)
        (submit-button "Opdater")))))

(defn edit-delivery-product [id]
  (let [p (if (not (nil? id)) (find-product id) nil)]
    (layout "Edit product" (html-header)
      (form-to [:post "/viewdeliveryproducts"]
        (hidden-field :update "true")
        (hidden-field :_id (:_id p))
        [:table
          [:tr [:td (label :name "Navn")] [:td (text-field :name (:name p))] [:td (label :delivery-type "Produkt type")] [:td (drop-down :delivery-type *delivery-type* (:delivery_type p))]]]
        (submit-button "Opdater")))))

(defn view-sales-products [req]
  (if (= "true" (get-in req [:params "create"]))
    (create-sales-product (struct sales-product (Integer/parseInt (get-in req [:params "id"])) (get-in req [:params "name"]) (get-in req [:params "sales-type"]) (get-in req [:params "weight"])
      (get-in req [:params "sortgroup"]) (get-in req [:params "sort"]) (get-in req [:params "delivery-products"]) (Integer. (get-in req [:params "binding-period"]))
      (struct sales-channels (get-in req [:params "start"]) (get-in req [:params "end"]) (get-in req [:params "portal-start"]) (get-in req [:params "portal-end"])
        (get-in req [:params "dealer-start"]) (get-in req [:params "dealer-end"]) (get-in req [:params "spoc-start"]) (get-in req [:params "spoc-end"])))))
  (if (= "true" (get-in req [:params "update"]))
    (update-sales-product (assoc (find-product (Integer/parseInt (get-in req [:params "id"]))) :name (get-in req [:params "name"])
      :sales_type (get-in req [:params "sales-type"]) :weight (get-in req [:params "weight"]) :sortgroup (get-in req [:params "sortgroup"]) :sort (get-in req [:params "sort"])
      :delivery_products (get-in req [:params "delivery-products"]) :binding_period (Integer. (get-in req [:params "binding-period"]))
      :sales_channels (struct sales-channels (get-in req [:params "start"]) (get-in req [:params "end"]) (get-in req [:params "portal-start"]) (get-in req [:params "portal-end"])
                             (get-in req [:params "dealer-start"]) (get-in req [:params "dealer-end"]) (get-in req [:params "spoc-start"]) (get-in req [:params "spoc-end"])))))
  (layout "Viev Products" (html-header)
    (html
      [:table
       [:tr [:th "Varenummer"] [:th "Produkt"]]
      (for [p (get-sales-products)]
        [:tr [:td (p 1)] [:td (p 0)] [:td [:a {:href (str "/editsalesproduct/" (p 1))} "Editer"]] [:td [:a {:href (str "/deletesalesproduct/" (p 1)) :onclick "return confirm('Er du sikker p&aelig; du vil slette');"} "Slet"]]])
    ])))

(defn view-delivery-products [req]
  (if (= "true" (get-in req [:params "create"]))
    (create-delivery-product (struct delivery-product (get-in req [:params "name"]) (get-in req [:params "delivery-type"]))))
  (if (= "true" (get-in req [:params "update"]))
    (update-delivery-product (assoc (find-product (get-in req [:params "_id"])) :name (get-in req [:params "name"]) :delivery_type (get-in req [:params "delivery-type"]))))
  (layout "Viev Products" (html-header)
    (html
      [:table
       [:tr [:th "ID"] [:th "Produkt"]]
      (for [p (get-delivery-products)]
        [:tr [:td (p 1)] [:td (p 0)] [:td [:a {:href (str "/editdeliveryproduct/" (p 1))} "Editer"]] [:td [:a {:href (str "/viewmeta/" (p 1))} "Se meta"]] [:td [:a {:href (str "/deletedeliveryproduct/" (p 1)) :onclick "return confirm('Er du sikker p&aelig; du vil slette');"} "Slet"]]])
    ])))


(defn addmeta [id]
  (let [p (find-product id)]
    (layout "Add Meta" (html-header)
      (form-to [:post "/savemeta"]
        (hidden-field :metaupdate "true")
        (hidden-field :id id)
        [:table
          [:tr [:td (drop-down :metakey *delivery-keys* nil)] [:td (text-field :metaval nil)]]]
        (submit-button "Opdater")))))

(defn savemeta [req]
  (let [id (get-in req [:params "id"])]
    (if (= "true" (get-in req [:params "metaupdate"]))
      (let [p (find-product id)]
        (update-delivery-product (with-meta p (conj {(get-in req [:params "metakey"]) (get-in req [:params "metaval"])} (meta p))))))
    (redirect (str "/viewmeta/" id))))

(defn viewmeta [id]
  (let [p (find-product id)]
    (layout "Se Meta" (html-header)
      (html
        [:table
         [:tr [:th "Key"] [:th "Value"]]
         (for [key (keys (meta p))]
          [:tr [:td key] [:td ((meta p) key)]])]
        [:a {:href (str "/addmeta/" id)} "Tilf&oslash;j meta"] [:br] [:a {:href "/viewdeliveryproducts"} "Vis leverings produkter"]))))

(defn viewpricebooks []
  (let [pricebooks (get-pricebooks)]
    (layout "Prisb&oslash;ger" (html-header)
      (html
        [:table
         [:tr [:th "Prisbog"]]
         (for [p pricebooks]
           [:tr [:td p]
                [:td [:a {:href (str "/addproducttopricebook/" p)} "Tilf&oslash;j produkt"]]
                [:td [:a {:href (str "/showproductsinpricebook/" p)} "Vis produkt priser"]]])]))))

(defn calculate-vat [general-price]
  (* 0.25 general-price))

(defn saveprice [pricebook-id product-id general-price koda radio copydan digi discount start-date end-date]
  (let [pricebook (find-pricebook pricebook-id)]
    (let [prices (:prices pricebook)]
      (let [prices-edited (filter #(not (= (Integer/parseInt product-id) (:product_id %))) prices)]
      (update-pricebook (assoc pricebook :prices (conj prices-edited (struct price (Integer/parseInt product-id) (Double/parseDouble general-price)
        (calculate-vat (Double/parseDouble general-price)) (Double/parseDouble koda) (Double/parseDouble radio) (Double/parseDouble copydan) (Double/parseDouble digi) (Double/parseDouble discount)
        (- (+ (Double/parseDouble general-price) (calculate-vat (Double/parseDouble general-price)) (Double/parseDouble koda) (Double/parseDouble radio)
          (Double/parseDouble copydan) (Double/parseDouble digi)) (Double/parseDouble discount)) start-date end-date)))))))
  (redirect (str "/showproductsinpricebook/" pricebook-id)))

(defn priceform [price]
  (html
    [:table
        [:tr [:td (label :product-id "Varenummer")] [:td (drop-down :product-id (get-sales-products) (:product_id price))]]
        [:tr [:td (label :general-price "Pris")] [:td (text-field :general-price (if (not (nil? price)) (:general_price price) 0))]]
        [:tr [:td (label :koda "Koda")] [:td (text-field :koda (if (not (nil? price)) (:koda price) 0))]]
        [:tr [:td (label :radio "RadiKoda")] [:td (text-field :radio (if (not (nil? price)) (:radio price) 0))]]
        [:tr [:td (label :copydan "CopyDan")] [:td (text-field :copydan (if (not (nil? price)) (:copydan price) 0))]]
        [:tr [:td (label :digi "Digital Rettigheder")] [:td (text-field :digi (if (not (nil? price)) (:digi price) 0))]]
        [:tr [:td (label :discount "Rabat")] [:td (text-field :discount (if (not (nil? price)) (:discount price) 0))]]
        [:tr [:td (label :start-date "Start Dato")] [:td (text-field :start-date (if (not (nil? price)) (:start_date price) (unparse custom-formatter (now))))]]
        [:tr [:td (label :end-date "Slut Dato")] [:td (text-field :end-date (if (not (nil? price)) (:end_date price) nil))]]]))

(defn add-product-to-pricebook [id]
  (layout (str "Tilf&oslash;j produkt til prisbog " id) (html-header)
    (form-to [:post "/saveprice"]
      (hidden-field :pricebook-id id)
      [:h2 (str "Tilf&oslash;j produkt til prisbog " id)]
      (priceform nil)
      (submit-button "Tilf&oslash;j pris"))))

(defn show-products-in-pricebook [id]
  (let [pricebook (find-pricebook id)]
    (layout (str "Vis produkter i prisbog " id) (html-header)
      [:table
        [:tr [:th "Varenummer"] [:th "Pris"] [:th "Koda"] [:th "RadiKoda"] [:th "CopyDan"] [:th "Digital Rettigheder"] [:th "Rabat"] [:th "Moms"] [:th "Total Pris"] [:th "Start Dato"] [:th "Slut Dato"]]
        (for [price (:prices pricebook)]
          [:tr [:td (:product_id price)] [:td (:general_price price)] [:td (:koda price)] [:td (:radio price)]
            [:td (:copydan price)] [:td (:digi price)] [:td (:discount price)] [:td (:vat price)] [:td (:total_price price)]
            [:td (:start_date price)] [:td (:end_date price)] 
            [:td [:a {:href (str "/editprice/" id "/" (:product_id price))} "Opdater pris"]]
            [:td [:a {:href (str "/deleteprice/" id "/" (:product_id price))} "Slet pris"]]])])))

(defn editprice [pricebook-id product-id type]
  (layout (str "Ret produktpris i prisbog " pricebook-id) (html-header)
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

(defn delete-sales-product [id]
  (delete-id id)
  (redirect "/viewsalesproducts"))

(defn delete-delivery-product [id]
  (delete-id id)
  (redirect "/viewdeliveryproducts"))