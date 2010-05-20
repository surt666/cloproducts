(ns abonnement.roles
  (:use clj-time.format))

(def built-in-formatter (formatters :basic-date-time))

(defn bestil-fra-lager [logistic-string]
  ;(println "IRIS" logistic-string)
  true)

(defn provisioner [prov-system prov-string date]
  ;(println prov-system prov-string (unparse built-in-formatter date))
  true)
