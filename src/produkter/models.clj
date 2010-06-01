(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct product :id :name :type :weight :sortgroup :sort :bundle_products :devoting_form)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct pricebook :name :prices)

;price should probably be extended with a type field so that you can have both a rent and buy price for a product
(defstruct price :product_id :type :general_price :vat :koda :radio :copydan :digi :discount :total_price)

(defstruct sales-concept :name :products)

(defstruct contract :name :sales_concept_name :pricebook)

(defstruct devoting-form :name :salestype :bindingperiod)

(def *property-keys*
  #{:prov_system :prov_string :logistic_string :port25 :prov_must_have_sn :portal_start :portal_end :dealer_start :dealer_end :spoc_start :spoc_end})

(def *sales-types*
  #{:buy :rent :buy_and_rent})

(def *price-types*
  #{"buy" "rent"})

(def *binding-periods*
  #{6 12})

(def *product-type*
  #{"tv" "bb" "tlf" "mobb" "dtv" "bundle"})