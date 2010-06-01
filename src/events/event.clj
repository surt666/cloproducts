(ns events.event)

(defstruct pris-event :betalings_aftale_id :ny_pris :rabat :aktiverings_dato :event_type :status)

(defstruct provisionerings-event :leverings_aftale_id :prov_system :prov_string :aktiverings_dato :event_type :status)

(defstruct logistik-event :leverings_aftale_id :logistic_string :aktiverings_dato :event_type :status)
