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

(defn create-product [product]
  (:_id (document-create host db (:id product) (assoc product :meta (meta product)))))

(defn create-subscription [aftale]
  (:_id (document-create host db aftale)))

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

(defn create-pricebook [pricebook]
  (:_id (document-create host db (:name pricebook) pricebook)))

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

(defn create-devoting-form [devoting-form]
  (:_id (document-create host db (:name devoting-form) devoting-form)))