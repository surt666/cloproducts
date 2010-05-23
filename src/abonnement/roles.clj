(ns abonnement.roles
  (:use clj-time.format))

(def built-in-formatter (formatters :basic-date-time))

(defn bestil-fra-lager [logistic-string]
  (do (println "IRIS" logistic-string)
  true))

(defn provisioner [prov-system prov-string date]
  (do (println prov-system prov-string date)
  true))
