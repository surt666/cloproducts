(ns abonnement.roles
  (:use clj-time.format
        repositories.couch-repository))

(def built-in-formatter (formatters :basic-date-time))

(defn bestil-fra-lager [logistik-event]
  (create-event logistik-event)
  true)

(defn provisioner [provisionerings-event]
  (create-event provisionerings-event)
  true)
