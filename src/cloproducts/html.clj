(ns cloproducts.html
  (:use cloproducts.roles
        cloproducts.models
        hiccup.page-helpers
        hiccup.core
        hiccup.form-helpers))

(defn layout [title header & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:meta {:http-equiv "X-UA-Compatible" :content "IE=EmulateIE7"}]
        [:title (str title)]]
        ;(stylesheet "ideadb.css")]
      [:body
        [:div {:id "header"} header]
        [:div {:id "main"}
          body]
        [:div {:id "footer"}
          "Totalt fed Footer"
        ]]]))

(defn present-sortgroup [sg]
  (let [x (map #(:value %) (get-sortgroup sg))]
    (html
      [:table
      (for [n x]
        [:tr[:td (radio-button sg false (:id n))][:td (:name n)][:td (:price n) " kr/md"]])])))

(defn index []
  (layout "TEST" "En header"
    (form-to [:POST "/mandatory"]
      (present-sortgroup "tva") (present-sortgroup "bba")
      (submit-button "Next"))))

(defn mandatory [tva bba]
  (let [order (struct order {} {:tva tva :bba bba})])
  (layout "TEST2" "En header"
    (html
      [:h2 (str tva "," bba)])
      (link-to "/sess" "sess")))

(defn sess [req]
  (layout "SESS" "En header"
    (println "REQ" req)
    (html
      [:h2 (str ((req :session) :tva))])))

