(ns abonnement.model)

(defstruct aftale :juridisk :leverings-aftaler :betalings-aftaler :status)

(defstruct leverings-aftale :produkt-id :leveringsPeriode :forbruger :installations-id :status :opgrader-leverings-aftale-id)

(defstruct betalings-aftale :produkt-id :pris :rabat :faktureringsPeriode :betaler :status)


