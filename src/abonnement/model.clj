(ns abonnement.model)

(defstruct abonnement :id :juridisk :faktureringsPeriode :leveringer :pris :rabat)

(defstruct leverings-aftale :abonnement-id :produkt-id :leveringsPeriode :forbruger :betaler)
