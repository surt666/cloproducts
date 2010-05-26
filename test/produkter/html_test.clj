(ns produkter.html-test
  (:use produkter.html
        produkter.models
        clojure.test))

(deftest price-for-product-normal
  (is (not (empty? (price-for-product 1101001 [(struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)]
    [(struct price 1201001 89.00 20 2.00 1.00 7.00 0 0 99.00) (struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)]))))
  (is (not (empty? (price-for-product 1201001 [(struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)] 
    [(struct price 1201001 89.00 20 2.00 1.00 7.00 0 0 99.00) (struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)])))))


(deftest price-for-product-with-prices-nil
  (is (not (empty? (price-for-product 1101001 nil [(struct price 1201001 89.00 20 2.00 1.00 7.00 0 0 99.00) (struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)])))))

(deftest price-for-product-with-prices-general-nil
  (is (not (empty? (price-for-product 1101001 [(struct price 1201001 89.00 20 2.00 1.00 7.00 0 0 99.00) (struct price 1101001 89.00 20 2.00 1.00 7.00 0 0 99.00)] nil)))))

(deftest price-for-product-with-prices-nil-and-price-not-in-general
  (is (empty? (price-for-product 1101001 nil [(struct price 1201001 89.00 20 2.00 1.00 7.00 0 0 99.00)]))))

(deftest price-for-product-with-all-prices-nil
  (is (empty? (price-for-product 1101001 nil nil))))