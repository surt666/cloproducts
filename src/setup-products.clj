(ns cloproducts.setup-products
  :use cloproducts.roles)

(defn create-products []
  (create-product (struct product 1101001 "Grundpakke" "tv" 1 "tva" 1 149.00 nil))
  (create-product (struct product 1101032 "Mellempakke" "tv" 2 "tva" 2 199.00 nil))
  (create-product (struct product 1101003 "Fuldpakke" "tv" 3 "tva" 3 279.00 nil))
  (create-product (struct product 1301001 "Bredbaand 4/1 Mbit/s" "bb" 1 "bba" 1 129.00 nil))
  (create-product (struct product 1301002 "Bredbaand 12/1 Mbit/s" "bb" 2 "bba" 2 149.00 nil))
  (create-product (struct product 1301003 "Bredbaand 20/2 Mbit/s" "bb" 3 "bba" 3 189.00 nil))
  (create-product (struct product 1301004 "Bredbaand 50/3 Mbit/s" "bb" 4 "bba" 4 249.00 nil)))

(defn create-products-mandatory []
  (create-product (struct product 1121001 "Oprettelse" "tv" 1 "tvs" 1 2049.00 nil))
  (create-product (struct product 1321001 "Standard Goer det selv" "bb" 1 "bbs" 1 129.00 nil))
  (create-product (struct product 1321002 "Traadloes Goer det selv" "bb" 2 "bbs" 2 149.00 nil))
  (create-product (struct product 1321003 "Standard Tekniker" "bb" 1 "bbt" 3 189.00 nil))
  (create-product (struct product 1321004 "Traadloes Tekniker" "bb" 2 "bbt" 4 249.00 nil)))

