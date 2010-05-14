(ns abonnement.product-repo)

(defstruct produkt :id :navn :pris :bundle-produkter)

(def produkter {1101001 (struct produkt 1101001 "Grundpakke" 149.00 nil)
                1101101 (struct produkt 1101101 "Mellempakke" 199.00 nil)
                1101201 (struct produkt 1101201 "Fuldpakke" 299.00 nil)
                1301101 (with-meta (struct produkt 1301101 "8 Mbit/s Bredbaand" 129.00 nil) {:prov_system "Stalone" :prov_string "1301101PROV"})
                1301002 (with-meta (struct produkt 1301002 "15 Mbit/s Bredbaand" 189.00 nil) {:prov_system "Stalone" :prov_string "1301002PROV"})
                1301003 (with-meta (struct produkt 1301003 "50 Mbit/s Bredbaand" 339.00 nil) {:prov_system "Stalone" :prov_string "1301003PROV"})
                1201001 (with-meta (struct produkt 1201001 "Digi Kort" 30.00 nil) {:prov_system "Sigma" :prov_string "1201001PROV" :logistic_string "1201001LOG"})
                1201101 (with-meta (struct produkt 1201101 "Digi Boks" 59.00 nil) {:logistic_string "1201101LOG"})
                1203101 (struct produkt 1203101 "YouSee Plus" 79.00 [1201001 1201101])
                1701001 (struct produkt 1701001 "YouSee Silver" 178.45 [1101001 1301101 1203101])})


(defn find-produkt [id]
  (produkter id))