(ns abonnement.model)

(defstruct abonnement :id :juridisk :leverings-aftaler :betalings-aftaler)

(defstruct leverings-aftale :abonnement-id :produkt-id :leveringsPeriode :forbruger)

(defstruct betalings-aftale :abonnement-id :pris :rabat :faktureringsPeriode :betaler)


