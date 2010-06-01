(ns abonnement.contexts
  (:use abonnement.model
        abonnement.roles
        events.event
        clojure.contrib.seq-utils
        clj-time.core
        clj-time.coerce
        repositories.couch-repository))

(defn filter-bp [bp]         
  [(filter #(nil? (:bundle-products (find-product %))) bp) (filter #(not (nil? (:bundle-products (find-product %)))) bp)])

(defn find-alle-bundle-produkter [bundle-produkter]
  (loop [result [] [p bp] bundle-produkter]
    (if (empty? bp)
      (flatten (conj result p))
     (recur (conj result p) (filter-bp (:bundle-products (find-product (first bp))))))))

(let [i (atom (first (get-highest-leverings-aftale-id)))]
  (defn get-la-idx []
    "Returns a distinct numeric ID for each call."
    (swap! i inc)))

(let [i (atom (first (get-highest-betalings-aftale-id)))]
  (defn get-ba-idx []
    "Returns a distinct numeric ID for each call."    
    (swap! i inc)))

(defn opret-leverings-aftale [produkt-id betalings-aftale-id forbruger inst-id]
  (let [prod (find-product produkt-id)]
    (let [prov-system (:prov_system (meta prod))
          prov-string (:prov_string (meta prod))
          logistic-string (:logistic_string (meta prod))
          leverings-periode [(.toString (to-date (now))) (.toString (to-date (now)))]]
      (if (not (nil? prov-system))
        (provisioner (struct provisionerings-event (rand-int 1000000000) prov-system prov-string (.toString (to-date (now))) :prov :prepared)))
      (if (not (nil? logistic-string))
        (bestil-fra-lager (struct logistik-event (rand-int 1000000000) logistic-string (.toString (to-date (now))) :logistic :prepared)))
      (struct leverings-aftale (get-la-idx) produkt-id leverings-periode forbruger inst-id :aktiv nil nil nil betalings-aftale-id))))  ;betalings-aftale-id saettes til produktnummer og skiftes senere til unikt id

(defn opret-alle-leverings-aftaler [produkt-ids forbruger inst-id]
  (for [produkt-id produkt-ids]
    (let [produkt (find-product produkt-id)]
      (let [bundle-ids (find-alle-bundle-produkter (filter-bp (:bundle_products produkt)))]
        (if (not (empty? bundle-ids))
          (for [pid bundle-ids]
            (opret-leverings-aftale pid produkt-id forbruger inst-id))
         (opret-leverings-aftale produkt-id nil forbruger inst-id))))))

(defn opret-alle-betalings-aftaler [produkt-ids prisbog betaler]
  (for [produkt-id produkt-ids]
    (let [fakturerings-periode [(.toString (to-date (now))) (.toString (to-date (now)))]]
      (struct betalings-aftale (get-ba-idx) produkt-id (:total_price (find-price prisbog produkt-id)) 0.0 fakturerings-periode betaler :aktiv "Beskrivelse"))))

(defn opdater-la-med-ba-id [la ba]
  (for [l la]
    (if (not (nil? (:betalings_aftale_id l)))
      (assoc l :betalings_aftale_id (:id (first (filter #(= (:betalings_aftale_id l) (:produkt_id %)) ba))))
      l)))

(defn opret-aftale [juridisk betaler forbruger prisbog produkt-ids inst-id]
  (let [l (doall (flatten (opret-alle-leverings-aftaler produkt-ids forbruger inst-id)))
        b (doall (opret-alle-betalings-aftaler produkt-ids prisbog betaler))]
    (let [ll (opdater-la-med-ba-id l b)]
      (let [a (struct aftale juridisk ll b :aktiv)]
        (create-subscription a)))))

(opret-aftale 111 222 333 "KAB" [1700002 1600001] 12345)