(ns produkter.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct product :id :name :type :weight :sortgroup :sort :price :bundle)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)

(defstruct prisbog :id :navn :priser)

(defstruct pris :id :produkt-id :generel-pris :koda :radio :copydan :digi :rabat :total-pris)