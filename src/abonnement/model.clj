(ns abonnement.model)

(defstruct aftale :juridisk :leverings_aftaler :betalings_aftaler :status)

(defstruct leverings-aftale :id :produkt_id :leverings_periode :forbruger :installations_id :status :opgraderet_af_leverings_aftale_id :properties :master_leverings_aftale_id :betalings_aftale_id)

(defstruct betalings-aftale :id :produkt_id :pris :rabat :fakturerings_periode :betaler :status :beskrivelse)


