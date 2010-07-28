(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct pricebook :name :prices)

(defstruct price :product_id :general_price :vat :koda :radio :copydan :digi :discount :total_price :start_date :end_date)

(defstruct sales-concept :name :products)

(defstruct contract :name :sales_concept_name :pricebook)

(defstruct delivery-product :name :delivery_type :channels)

(defstruct sales-product :id :name :sales_type :weight :sortgroup :sort :delivery_products :sales_products :binding_period :sales_channels)

(defstruct sales-channels :start :end :portal_start :portal_end :dealer_start :dealer_end :spoc_start :spoc_end)

(defstruct channel :name :channel_type)

(def *delivery-keys*
  #{:prov_system :prov_string :logistic_string :port25 :prov_must_have_sn})

(def *sales-types*
  #{"Engangsydelse" "Abonnement" "Bundle"})

(def *binding-periods*
  #{0 6 12})

(def *delivery-type*
  #{"CLEAR" "DTV" "BB" "TLF" "MoBB" "HW m. SN" "HW u. SN"})

(def *channel-type*
  #{"TV" "RADIO"})