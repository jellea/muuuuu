(ns muuuuu.utils
  (:require [cljs.reader :as reader])
  (:import [goog.ui IdGenerator]))

(defn value-from-node
  "Gets value of a DOM input element"
  [component field]
  (let [n (om/get-node component field)
        v (-> n .-value clojure.string/trim)]
    (when-not (empty? v)
      [v n])))

(defn clear-nodes!
  "Clear specified DOM input elements"
  [& nodes]
  (doall (map #(set! (.-value %) "") nodes)))

(defn guid []
  "Gives unique id to be used by React"
  (.getNextUniqueId (.getInstance IdGenerator)))

(def lastUsedColor (atom -1 {:validator #()}))

(def genrelist (list "acoustic" "ambient" "blues" "classical" "country" "electronic" "emo" "folk" "hardcore" "hip hop" "indie" "jazz" "latin" "metal" "pop" "pop punk" "punk" "reggae" "rnb" "rock" "soul" "world" "60s" "70s" "80s" "90s" ))

(def usernames ["Ryoji Ikeda" "Richie Hawtin" "Nathan Fake" "Jimi Hendrix" "Alva Noto" "Speedy J" "Aphex Twin" "Mike Dehnert" "Luke Abbott" "John Coltrane" "Kangding Ray" "Electric Wizard" "Frédéric Chopin"])

(def make-roomslist
  (apply hash-map
   (flatten (map (fn [r]
     (list r {:usercount (rand-int 133) :id (guid)}))
                 genrelist))))

(def colors [{:hex "34495e" :bright false} {:hex "16a085" :bright false} {:hex "27ae60"  :bright false}
             {:hex "2980b9" :bright false} {:hex "8e44ad" :bright false} {:hex "2c3e50" :bright false}
             {:hex "f1c40f"} {:hex "e67e22"} {:hex "e74c3c" :bright false}
             {:hex "ecf0f1"} {:hex "95a5a6"} {:hex "f39c12"}
             {:hex "d35400" :bright false} {:hex "c0392b" :bright false} {:hex "bdc3c7"}
             {:hex "7f8c8d" :bright false} {:hex "1abc9c"} {:hex "2ecc71"}
             {:hex "3498db"} {:hex "9b59b6"}])

(defn get-next-color "Returns next color as a string" []
  (swap! lastUsedColor #(if (< % 19) (inc %) 0))
  (nth colors @lastUsedColor)
)

(defn current-room [rooms-state]
  (first (filter (fn [r] (= (:inviewport (second r)) true)) rooms-state))
)

(defn get-active-rooms "Retursn a list of only active rooms" [rooms]
  (filter #(true? (:active (second %))) rooms))
