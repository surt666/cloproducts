(ns cloproducts.roles
  (:use couchdb.client))

(defn buy [customer product]
  (println "Buyer" (:name customer) "just bought" (:name product)))

(defn rent [offeree product]
  (println "Offeree" (:name offeree) "rents" (:name product)))

(defn offeree-for [offeree product]
  (println (:name offeree) "just became an offeree for rent product:" (:name product)))

(defn enduser-for [enduser product]
  (println (:name enduser) "just became an enduser for rent product:" (:name product)))

(def host "http://localhost:5984/")

(def db "test")

(defn create-customer [customer]
  (:_id (document-create host db customer)))

(defn create-address [address]
  (:_id (document-create host db address)))

(defn create-order [order]
  (:_id (document-create host db order)))

(defn create-product [product]
  (:_id (document-create host db product)))

(defn find-product [id]
  (document-get host db id))

(defn get-sortgroup [sg]
  (:rows (view-get host db "views" "get_sortgroup" {:startkey [sg] :endkey [sg {}]})))