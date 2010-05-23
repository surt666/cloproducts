(ns abonnement.contexts
  (:use abonnement.model
        abonnement.roles
        abonnement.product-repo
        clojure.contrib.seq-utils
        clj-time.core
        clj-time.coerce
        repositories.couch-repository))

(defn filter-bp [bp]         
  [(filter #(nil? (:bundle-produkter (find-produkt %))) bp) (filter #(not (nil? (:bundle-produkter (find-produkt %)))) bp)])

(defn find-alle-bundle-produkter [bundle-produkter]
  (loop [result [] [p bp] bundle-produkter]
    (if (empty? bp)
      (flatten (conj result p))
     (recur (conj result p) (filter-bp (:bundle-produkter (find-produkt (first bp))))))))

(defn opret-leverings-aftale [produkt-id forbruger inst-id]
  (let [prod (find-produkt produkt-id)]
    (let [prov-system (:prov_system (meta prod))
          prov-string (:prov_string (meta prod))
          logistic-string (:logistic_string (meta prod))
          leverings-periode [(to-long (now)) (to-long (now))]]
      (if (not (nil? prov-system))
        (provisioner prov-system prov-string (to-long (now))))
      (if (not (nil? logistic-string))
        (bestil-fra-lager logistic-string))
      (struct leverings-aftale produkt-id leverings-periode forbruger inst-id :aktiv nil))))

(defn opret-alle-leverings-aftaler [produkt-ids forbruger inst-id]
  (for [produkt-id produkt-ids]
    (let [produkt (find-produkt produkt-id)]
      (let [bundle-ids (find-alle-bundle-produkter (filter-bp (:bundle-produkter produkt)))]
        (if (not (empty? bundle-ids))
          (for [pid bundle-ids]
            (opret-leverings-aftale pid forbruger inst-id))
         (opret-leverings-aftale produkt-id forbruger inst-id))))))

(defn opret-betalings-aftale [produkt-id betaler fakturerings-periode]
  (let [prod (find-produkt produkt-id)]
    (struct betalings-aftale produkt-id (:pris prod) nil fakturerings-periode betaler :aktiv)))

(defn opret-alle-betalings-aftaler [produkt-ids betaler]
  (for [produkt-id produkt-ids]
    (let [fakturerings-periode [(to-long (now)) (to-long (now))]]
      (opret-betalings-aftale produkt-id betaler fakturerings-periode))))

(defn opret-aftale [juridisk betaler forbruger produkt-ids inst-id]
  (let [l (flatten (opret-alle-leverings-aftaler produkt-ids forbruger inst-id))]
    (let [f (opret-alle-betalings-aftaler produkt-ids betaler)]
      (let [a (struct aftale juridisk l f :aktiv)]
        (println "Persister abon" (:id a))
        (println "Persister betalings-aftaler" (:betalings-aftaler a))
        (println "Persister leverings-aftaler" (:leverings-aftaler a))
       (create-subscription a)))))

(opret-aftale 111 222 333 [1701001 1301201] 12345)