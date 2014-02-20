(ns muuuuu.utils
  (:require [cljs.reader :as reader])
  (:import [goog.ui IdGenerator]))

(defn value-from-node
  [component field]
  (let [n (om/get-node component field)
        v (-> n .-value clojure.string/trim)]
    (when-not (empty? v)
      [v n])))

(defn clear-nodes!
  [& nodes]
  (doall (map #(set! (.-value %) "") nodes)))

(defn guid []
  (.getNextUniqueId (.getInstance IdGenerator)))

(def lastUsedColor (atom -1 {:validator #()}))

(def colors [{:hex "34495e" :bright false} {:hex "16a085"} {:hex "27ae60"  :bright false}
             {:hex "2980b9" :bright false} {:hex "8e44ad" :bright false} {:hex "2c3e50" :bright false}
             {:hex "f1c40f"} {:hex "e67e22"} {:hex "e74c3c" :bright false}
             {:hex "ecf0f1"} {:hex "95a5a6"} {:hex "f39c12"}
             {:hex "d35400" :bright false} {:hex "c0392b" :bright false} {:hex "bdc3c7"}
             {:hex "7f8c8d" :bright false} {:hex "1abc9c"} {:hex "2ecc71"}
             {:hex "3498db"} {:hex "9b59b6"}])

(defn get-next-color "Return next color as a string" []
  (swap! lastUsedColor #(if (< % 19) (inc %) 0))
  (nth colors @lastUsedColor)
)
