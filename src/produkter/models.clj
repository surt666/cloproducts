(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

;(defstruct product :id :name :type :weight :sortgroup :sort :bundle_products :devoting_form)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct pricebook :name :prices)

(defstruct price :product_id :general_price :vat :koda :radio :copydan :digi :discount :total_price)

(defstruct sales-concept :name :products)

(defstruct contract :name :sales_concept_name :pricebook)

(defstruct delivery-product :name :delivery_type)

(defstruct sales-product :id :name :sales_type :weight :sortgroup :sort :delivery_products :binding_period :sales_channels)

(defstruct sales-channels :start :end :portal_start :portal_end :dealer_start :dealer_end :spoc_start :spoc_end)

;(defstruct devoting-form :name :salestype :bindingperiod)

(def *delivery-keys*
  #{:prov_system :prov_string :logistic_string :port25 :prov_must_have_sn})

(def *sales-types*
  #{"Engangs" "Abon"})

(def *binding-periods*
  #{0 6 12})

(def *delivery-type*
  #{"TV" "DTV" "BB" "TLF" "MoBB" "Bundle" "Kanal" "HW m. SN" "HW u. SN"})