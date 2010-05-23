(ns setup-products
  (:use repositories.couch-repository
       produkter.models))

(defn create-products []
  (create-product (struct product 1101001 "Grundpakke" "tv" 1 "tva" 1 nil :rent))
  (create-product (struct product 1101032 "Mellempakke" "tv" 2 "tva" 2 nil :rent))
  (create-product (struct product 1101003 "Fuldpakke" "tv" 3 "tva" 3 nil :rent))
  (create-product (with-meta (struct product 1301001 "Bredbaand 8/1 Mbit/s" "bb" 1 "bba" 1 nil :rent) {:prov_system "Stalone" :prov_string "1301001PROV"}))
  (create-product (with-meta (struct product 1301002 "Bredbaand 15/1 Mbit/s" "bb" 2 "bba" 2 nil :rent) {:prov_system "Stalone" :prov_string "1301002PROV"}))
  (create-product (with-meta (struct product 1301003 "Bredbaand 20/2 Mbit/s" "bb" 3 "bba" 3 nil :rent) {:prov_system "Stalone" :prov_string "1301003PROV"}))
  (create-product (with-meta (struct product 1301004 "Bredbaand 50/3 Mbit/s" "bb" 4 "bba" 4 nil :rent) {:prov_system "Stalone" :prov_string "1301004PROV"})))

(defn create-products-mandatory []
  (create-product (struct product 1121001 "Oprettelse" "tv" 1 "tvs" 1 nil :buy))
  (create-product (struct product 1321001 "Standard Goer det selv" "bb" 1 "bbs" 1 nil :buy))
  (create-product (struct product 1321002 "Traadloes Goer det selv" "bb" 2 "bbs" 2 nil :buy))
  (create-product (struct product 1321003 "Standard Tekniker" "bb" 1 "bbt" 3 nil :buy))
  (create-product (struct product 1321004 "Traadloes Tekniker" "bb" 2 "bbt" 4 nil :buy)))

(defn create-pricebooks []
  (let [prices [(struct price 1101001 89.00 2.00 1.00 7.00 0 0 99.00)
                (struct price 1101032 119.00 2.00 1.00 7.00 0 0 129.00)
                (struct price 1101003 179.00 2.00 1.00 7.00 0 0 189.00)
                (struct price 1301001 99.00 0 0 0 0 0 99.00)
                (struct price 1301002 129.00 0 0 0 0 0 129.00)
                (struct price 1301003 169.00 0 0 0 0 0 169.00)
                (struct price 1301004 199.00 0 0 0 0 0 199.00)
                (struct price 1121001 100.00 0 0 0 0 0 100.00)
                (struct price 1321001 0 0 0 0 0 0 0)
                (struct price 1321002 0 0 0 0 0 0 0)
                (struct price 1321003 699.00 0 0 0 0 0 699.00)
                (struct price 1321003 699.00 0 0 0 0  0699.00)]]
    (let [pricebook (struct pricebook "YouSee" prices)]
      (create-pricebook pricebook)))
  (let [prices [(struct price 1101001 74.00 2.00 1.00 7.00 0 0 84.00)
                (struct price 1101032 109.00 2.00 1.00 7.00 0 0 119.00)
                (struct price 1101003 159.00 2.00 1.00 7.00 0 0 169.00)]]
    (let [pricebook (struct pricebook "KAB" prices)]
      (create-pricebook pricebook))))

(defn create-sales-concepts []
  (create-sales-concept (struct sales-concept "IER" [1101001 1101032 1101003 1301001 1301002 1301003 1301004 1121001 1321001 1321002 1321003 1321004]))
  (create-sales-concept (struct sales-concept "FF" [1101001 1101032 1101003 1301001 1301002 1301003 1121001 1321002 1321004])))

(defn create-contracts []
  (create-contract (struct contract "1234567" "IER" "YouSee"))
  (create-contract (struct contract "7654321" "FF" "KAB")))

(defn create-devoting-forms []
  (create-devoting-form (struct devoting-form :rent-6 :rent 6))
  (create-devoting-form (struct devoting-form :buy :buy 6))
  (create-devoting-form (struct devoting-form :rent-and-buy :rent-and-buy 6))
  (create-devoting-form (struct devoting-form :rent-12 :rent 12)))