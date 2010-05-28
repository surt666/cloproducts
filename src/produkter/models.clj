(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct product :id :name :type :weight :sortgroup :sort :bundle-products :devoting-form)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct pricebook :name :prices)

(defstruct price :product-id :general-price :vat :koda :radio :copydan :digi :discount :total-price)

(defstruct sales-concept :name :products)

(defstruct contract :name :sales-concept-name :pricebook)

(defstruct devoting-form :name :salestype :bindingperiod)

(def *property-keys*
  #{:prov_system :prov_string :logistic_string :port25 :prov_must_have_sn})

(def *sales-types*
  #{:buy :rent :buy-and-rent})

(def *binding-periods*
  #{6 12})

(def *product-type*
  #{"tv" "bb" "tlf" "mobb" "dtv" "bundle"})