(ns abonnement.contexts
  (:use abonnement.model
        abonnement.roles
        abonnement.product-repo
        clojure.contrib.seq-utils
        clj-time.core))

(defn filter-bp [bp]         
  [(filter #(nil? (:bundle-produkter (find-produkt %))) bp) (filter #(not (nil? (:bundle-produkter (find-produkt %)))) bp)])

(defn find-alle-bundle-produkter [bundle-produkter]
  (loop [result [] [p bp] bundle-produkter]
    (if (empty? bp)
      (flatten (conj result p))
     (recur (conj result p) (filter-bp (:bundle-produkter (find-produkt (first bp))))))))

(defn opret-leverings-aftale [produkt-id abon-id forbruger inst-id]
  (let [prod (find-produkt produkt-id)]
    (let [prov-system (:prov_system (meta prod))
          prov-string (:prov_string (meta prod))
          logistic-string (:logistic_string (meta prod))
          leverings-periode [(now) (now)]]
      (if (not (nil? prov-system))
        (provisioner prov-system prov-string (now)))
      (if (not (nil? logistic-string))
        (bestil-fra-lager logistic-string))
      (struct leverings-aftale nil abon-id produkt-id leverings-periode forbruger inst-id :aktiv nil))))

(defn opret-alle-leverings-aftaler [produkt-ids abon-id forbruger inst-id]
  (for [produkt-id produkt-ids]
    (let [produkt (find-produkt produkt-id)]
      (let [bundle-ids (find-alle-bundle-produkter (filter-bp (:bundle-produkter produkt)))]
        (if (not (empty? bundle-ids))
          (for [pid bundle-ids]
            (opret-leverings-aftale pid abon-id forbruger inst-id))
         (opret-leverings-aftale produkt-id abon-id forbruger inst-id))))))

(defn opret-betalings-aftale [produkt-id abon-id betaler fakturerings-periode]
  (let [prod (find-produkt produkt-id)]
    (struct betalings-aftale nil abon-id produkt-id (:pris prod) nil fakturerings-periode betaler :aktiv)))

(defn opret-alle-betalings-aftaler [produkt-ids abon-id betaler]
  (for [produkt-id produkt-ids]
    (let [fakturerings-periode [(now) (now)]]
      (opret-betalings-aftale produkt-id abon-id betaler fakturerings-periode))))

(defn opret-aftale [abon-id juridisk betaler forbruger produkt-ids inst-id]
  (let [l (opret-alle-leverings-aftaler produkt-ids abon-id forbruger inst-id)]
    (let [f (opret-alle-betalings-aftaler produkt-ids abon-id betaler)]
      (let [a (struct aftale abon-id juridisk l f :aktiv)]
        (println "Persister abon" (:id a))
        (println "Persister betalings-aftaler" (:betalings-aftaler a))
        (println "Persister leverings-aftaler" (:leverings-aftaler a))
       ))))

(opret-aftale 3 111 222 333 [1701001 1301201] 12345)