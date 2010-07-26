(ns ordre.dsl-test
  (:use clojure.test
        ordre.dsl))

(deftest dsl-test
  (is (order
         (:saleschannel "F")
         (:customer 1234556)
         (:payer 232321)
         (:letters {:legal true :payer false})
         (:system "PORTAL")
         (:agreement [(:new-agreement
                        {:products [(121212) (12131313 213234212)]})
                      (:change-agreement
                        {:agreement-number 726376812
                         :products [(1212121 1213235)]})])
    )))
