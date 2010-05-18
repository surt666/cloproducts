(ns cloproducts.roles)

(defn buy [customer product]
  (println "Buyer" (:name customer) "just bought" (:name product)))

(defn rent [offeree product]
  (println "Offeree" (:name offeree) "rents" (:name product)))

(defn offeree-for [offeree product]
  (println (:name offeree) "just became an offeree for rent product:" (:name product)))

(defn enduser-for [enduser product]
  (println (:name enduser) "just became an enduser for rent product:" (:name product)))

