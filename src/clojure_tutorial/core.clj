(ns clojure-tutorial.core
  (:import [java.text SimpleDateFormat]
           [com.curry.expenses Expense]))

(defn new-expense [date-string dollars cents category merchant-name]
  {:date (.parse (SimpleDateFormat. "yyyy-MM-dd") date-string)
   :amount-dollars dollars
   :amount-cents cents
   :category category
   :merchant-name merchant-name})

(defmulti total-cents class)
(defmethod total-cents clojure.lang.IPersistentMap [e]
  (-> (:amount-dollars e)
      (* 100)
      (+ (:amount-cents e))))
(defmethod total-cents com.curry.expenses.Expense [e]
  (.amountInCents e))

(defmulti is-category? (fn [e category] (class e)))
(defmethod is-category? clojure.lang.IPersistentMap [e some-category]
  (= (:category e) some-category))
(defmethod is-category? com.curry.expenses.Expense [e some-category]
  (= (.getCategory e) some-category))

(defn category-is [category]
  #(is-category? % category))

(defn total-amount
  ([expenses-list]
   (total-amount (constantly true) expenses-list))
  ([pred expenses-list]
   (->> expenses-list
        (filter pred)
        (map total-cents)
        (apply +))))

(def java-expenses [(Expense. "2009-8-24" 44 95 "books" "amazon.com")
                    (Expense. "2009-8-25" 29 11 "gas" "shell")])

(defn -main []
  (let [total-cents (map #(.amountInCents %) java-expenses)]
    (println (apply + total-cents)))
  (println (class (nth java-expenses 1)))
  (println (total-amount (category-is "books") java-expenses)))
