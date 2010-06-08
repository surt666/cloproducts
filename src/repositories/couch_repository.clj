(ns repositories.couch-repository
  (:use couchdb.client
        abonnement.model))

(def host "http://localhost:5984/")

(def db "test")

(defn create-customer [customer]
  (:_id (document-create host db customer)))

(defn create-address [address]
  (:_id (document-create host db address)))

(defn create-order [order]
  (:_id (document-create host db order)))

(defn create-delivery-product [product]
  (:_id (document-create host db (assoc product :meta (meta product)))))

(defn create-sales-product [product]
  (:_id (document-create host db (:id product) product)))

(defn update-sales-product [product]
  (:_id (document-update host db (:id product) (assoc product :meta (meta product)))))

(defn update-delivery-product [product]
  (:_id (document-update host db (:_id product) (assoc product :meta (meta product)))))

(defn create-subscription [aftale]
  (:_id (document-create host db aftale)))

(defn create-event [event]
  (:_id (document-create host db event)))

(defn add-meta [p]
  (let [meta (:meta p)]
  (if (not (nil? meta))
    (dissoc (with-meta p meta) :meta)
    p)))

(defn find-product [id]
  (add-meta
    (document-get host db id)))

(defn get-sortgroup [sg]
  (map add-meta 
    (map #(:value %)
      (:rows (view-get host db "views" "get_sortgroup" {:startkey [sg] :endkey [sg {}]})))))

;(defn get-devoting-forms []
;  (map #(:key %)
;    (:rows (view-get host db "views" "get_devoting_form"))))

(defn get-sales-products []
  (map #(:key %)
    (:rows (view-get host db "views" "get_sales_products"))))

(defn get-delivery-products []
  (map #(:key %)
    (:rows (view-get host db "views" "get_delivery_products"))))


(defn create-pricebook [pricebook]
  (:_id (document-create host db (:name pricebook) pricebook)))

(defn update-pricebook [pricebook]
  (:_id (document-update host db (:name pricebook) pricebook)))

(defn create-sales-concept [sales-concept]
  (:_id (document-create host db (:name sales-concept) sales-concept)))

(defn create-contract [contract]
  (:_id (document-create host db (:name contract) contract)))

(defn find-contract [contract]
  (document-get host db contract))

(defn find-sales-concept [sales-concept]
  (document-get host db sales-concept))

(defn find-pricebook [pricebook]
  (document-get host db pricebook))

(defn find-price [pricebook product-id]
  (let [p (find-pricebook pricebook)]
    (let [price (first (filter #(= product-id (:product-id %)) (:prices p)))]
      (if (not (nil? price))
        price
        (let [y (find-pricebook "YouSee")]
          (first (filter #(= product-id (:product-id %)) (:prices y))))))))

(defn create-devoting-form [devoting-form]
  (:_id (document-create host db (:name devoting-form) devoting-form)))

(defn get-pricebooks []
  (map #(:key %)
    (:rows (view-get host db "views" "get-pricebooks"))))

(defn get-highest-leverings-aftale-id []
  (map #(:value %)
      (:rows (view-get host db "views" "leverings_sequence"))))

(defn get-highest-betalings-aftale-id []
  (map #(:value %)
      (:rows (view-get host db "views" "betalings_sequence"))))
