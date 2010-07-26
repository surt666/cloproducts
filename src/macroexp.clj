(ns macroexp)

(defmacro aftale [a func]
  `(let [an# (:aftalenummer ~a)
         fo#   (:forretnings-omraade ~a)
         na#   (:abonnementer ~a)]
      (~func an# fo# na#)))

(defn opret [an fo na]
  (println "Opretter" an fo na))

(defn opsig [an fo na]
  (println "Opsig" an fo na))

(defmacro unless [test & exprs]
 `(if (not ~test)
    (do ~@exprs)))

(defmacro nif [expr pos zero neg]
  `(let [g# ~expr]
    (cond
      (pos? g#) ~pos
      (zero? g#) ~zero
      :default ~neg)))
