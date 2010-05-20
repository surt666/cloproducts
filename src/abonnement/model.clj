(ns abonnement.model)

(defstruct aftale :id :juridisk :leverings-aftaler :betalings-aftaler :status)

(defstruct leverings-aftale :id :abonnement-id :produkt-id :leveringsPeriode :forbruger :installations-id :status :opgrader-leverings-aftale-id)

(defstruct betalings-aftale :id :abonnement-id :produkt-id :pris :rabat :faktureringsPeriode :betaler :status)


