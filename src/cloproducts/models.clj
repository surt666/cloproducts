(ns cloproducts.models)

(defstruct contact :contact-id :name)

(defstruct prod :product-id :name :type)

(defstruct product :id :name :type :weight :sortgroup :sort :price :bundle)

(defstruct customer :id :firstname :lastname :email :address)

(defstruct address :id :city :zip :street :housenr :floor :side)

(defstruct order :customer :products)
