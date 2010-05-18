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

(defn opret-leverings-aftale [abon-id forbruger produkt-id prov-system prov-string logistic-string leverings-periode]
  (if (not (nil? prov-system))
    (provisioner prov-system prov-string (now)))
  (if (not (nil? logistic-string))
    (bestil-fra-lager logistic-string))
  (struct leverings-aftale abon-id produkt-id leverings-periode forbruger))

(defn find-alle-leverings-aftaler [produkt abon-id betaler forbruger]
  (let [b (find-alle-bundle-produkter (filter-bp (:bundle-produkter produkt)))]
    (let [plist (cons (:id produkt) b)]
      (for [p plist]
        (let [prod (find-produkt p)]
          (let [prov-system (:prov_system (meta prod))
                prov-string (:prov_string (meta prod))
                logistic-string (:logistic_string (meta prod))
                leverings-periode [(now) (now)]]
            (opret-leverings-aftale abon-id forbruger p prov-system prov-string logistic-string leverings-periode)))))))

(defn opret-abonnement [abon-id juridisk betaler forbruger produktId]
  (let [p (find-produkt produktId)]
    (let [l (find-alle-leverings-aftaler p abon-id betaler forbruger)]
      (let [a (struct abonnement abon-id juridisk l nil)]
        (println "Persister" (:id a))
        a))))

(opret-abonnement 3 111 222 333 1701001)
