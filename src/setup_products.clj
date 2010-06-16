(ns setup-products
  (:use repositories.couch-repository
        produkter.models
        clj-time.core
        clj-time.coerce))

(defn create-sales-products []
  (create-sales-product (struct sales-product 1101001 "Grundpakke" "Abon" "1" "tva" "1" (vector (create-delivery-product (struct delivery-product "Grundpakke" "TV"))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1101032 "Mellempakke" "Abon" "2" "tva" "2" (vector (create-delivery-product (struct delivery-product "Mellempakke" "TV"))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1101003 "Fuldpakke" "Abon" "3" "tva" "3" (vector (create-delivery-product (struct delivery-product "Fuldpakke" "TV"))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1301001 "Bredbaand 8/1 Mbit/s" "Abon" "1" "bba" "1" (vector (create-delivery-product (with-meta (struct delivery-product "Bredbaand 8/1 Mbit/s" "BB") {:prov_system "Stalone" :prov_string "1301001PROV"}))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1301002 "Bredbaand 15/1 Mbit/s" "Abon" "2" "bba" "2" (vector (create-delivery-product (with-meta (struct delivery-product "Bredbaand 15/1 Mbit/s" "BB") {:prov_system "Stalone" :prov_string "1301002PROV"}))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1301003 "Bredbaand 20/2 Mbit/s" "Abon" "3" "bba" "3" (vector (create-delivery-product (with-meta (struct delivery-product "Bredbaand 20/2 Mbit/s" "BB") {:prov_system "Stalone" :prov_string "1301003PROV"}))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1301004 "Bredbaand 50/3 Mbit/s" "Abon" "4" "bba" "4" (vector (create-delivery-product (with-meta (struct delivery-product "Bredbaand 50/3 Mbit/s" "BB") {:prov_system "Stalone" :prov_string "1301004PROV"}))) 6 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1121001 "Oprettelse" "Engangs" "1" "tvs" "1" (vector (create-delivery-product (struct delivery-product "Oprettelse" "TV"))) 0 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1321001 "Standard Goer det selv" "Engangs" "1" "bbm" "1" (vector (create-delivery-product (struct delivery-product "Standard Goer det selv" "BB"))) 0 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1321002 "Traadloes Goer det selv" "Engangs" "1" "bbm" "2" (vector (create-delivery-product (struct delivery-product "Traadloes Goer det selv" "BB"))) 0 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1321003 "Standard Tekniker" "Engangs" "1" "bbm" "3" (vector (create-delivery-product (struct delivery-product "Standard Tekniker" "BB"))) 0 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil)))
  (create-sales-product (struct sales-product 1321004 "Traadloes Tekniker" "Engangs" "1" "bbm" "4" (vector (create-delivery-product (struct delivery-product "Traadloes Tekniker" "BB"))) 0 (struct sales-channels (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil (.toString (to-date (now))) nil))))

(defn calculate-vat [general-price]
  (* 0.25 general-price))

(defn create-pricebooks []
  (let [prices [(struct price 1101001 89.00 (calculate-vat 89.00) 2.00 1.00 7.00 0 0 99.00 (.toString (to-date (now))) nil)
                (struct price 1101032 119.00 (calculate-vat 119.00) 2.00 1.00 7.00 0 0 129.00 (.toString (to-date (now))) nil)
                (struct price 1101003 179.00 (calculate-vat 179.00) 2.00 1.00 7.00 0 0 189.00 (.toString (to-date (now))) nil)
                (struct price 1301001 99.00 (calculate-vat 99.00) 0 0 0 0 0 99.00 (.toString (to-date (now))) nil)
                (struct price 1301002 129.00 (calculate-vat 129.00)0 0 0 0 0 129.00 (.toString (to-date (now))) nil)
                (struct price 1301003 169.00 (calculate-vat 169.00) 0 0 0 0 0 169.00 (.toString (to-date (now))) nil)
                (struct price 1301004 199.00 (calculate-vat 199.00) 0 0 0 0 0 199.00 (.toString (to-date (now))) nil)
                (struct price 1121001 100.00 (calculate-vat 100.00) 0 0 0 0 0 100.00 (.toString (to-date (now))) nil)
                (struct price 1321001 0 0 0 0 0 0 0 0 (.toString (to-date (now))) nil)
                (struct price 1321002 0 0 0 0 0 0 0 0 (.toString (to-date (now))) nil)
                (struct price 1321003 699.00 (calculate-vat 699.00) 0 0 0 0 0 699.00 (.toString (to-date (now))) nil)]]
    (let [pricebook (struct pricebook "YouSee" prices)]
      (create-pricebook pricebook)))
  (let [prices [(struct price 1101001 74.00 (calculate-vat 74.00) 2.00 1.00 7.00 0 0 84.00 (.toString (to-date (now))) nil)
                (struct price 1101032 109.00 (calculate-vat 109.00) 2.00 1.00 7.00 0 0 119.00 (.toString (to-date (now))) nil)
                (struct price 1101003 159.00 (calculate-vat 159.00) 2.00 1.00 7.00 0 0 169.00 (.toString (to-date (now))) nil)]]
    (let [pricebook (struct pricebook "KAB" prices)]
      (create-pricebook pricebook))))

(defn create-sales-concepts []
  (create-sales-concept (struct sales-concept "IER" [1101001 1101032 1101003 1301001 1301002 1301003 1301004 1121001 1321001 1321002 1321003 1321004]))
  (create-sales-concept (struct sales-concept "FF" [1101001 1101032 1101003 1301001 1301002 1301003 1121001 1321002 1321004])))

(defn create-contracts []
  (create-contract (struct contract "1234567" "IER" "YouSee"))
  (create-contract (struct contract "7654321" "FF" "KAB")))

(defn create-all []
    (create-sales-products)
    (create-pricebooks)
    (create-sales-concepts)
    (create-contracts))
