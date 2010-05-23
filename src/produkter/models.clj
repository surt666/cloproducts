(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct product :id :name :type :weight :sortgroup :sort :bundle)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct pricebook :name :prices)

(defstruct price :product-id :general-price :koda :radio :copydan :digi :discount :total-price)

(defstruct sales-concept :name :products)

(defstruct contract :name :sales-concept :pricebook)