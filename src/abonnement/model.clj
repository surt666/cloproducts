(ns abonnement.model)

(defstruct abonnement :id :juridisk :faktureringsPeriode :leveringer :pris :rabat)

(defstruct leverings-aftale :abonnementId :produktId :leveringsPeriode :forbruger :betaler)
