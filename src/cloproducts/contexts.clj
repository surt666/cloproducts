(ns clotest.contexts
   (:use clotest.models clotest.roles))

(alias 'prod 'clotest.models)

(defmulti place-order (fn [buyer product & customers] (:type product)))

(defmethod place-order ::prod/Sale [buyer product]
  (println "Contact" (:name buyer) "became a customer and just places an order, he want to buy" (:name product))
  (buy buyer product))

(defmethod place-order ::prod/Rent [offeree product & customers]
  (println "A rent order was placed")
  (offeree-for offeree product)
  (enduser-for (first customers) product)
  (rent offeree product)
  (println "Order completed..."))

(defn place-order-sale [buyer-id product-id]
  (let [buyer (struct contact buyer-id "Steen")
        prod (struct product product-id "En koebeboks" ::prod/Sale)]
    (place-order buyer prod)))

(defn place-order-rent [offeree-id enduser-id product-id]
  (let [offeree (struct contact offeree-id "Steen")
        enduser (struct contact enduser-id "Peter")
        prod (struct product product-id "En lejeboks" ::prod/Rent)]
    (place-order offeree prod enduser)))

(place-order-sale 111 222)

(place-order-rent 111 222 333)