(ns muuuuu.mock
  (:require [goog.Timer :as Timer]
            [om.core :as om :include-macros true]
            [muuuuu.utils :refer [get-active-rooms guid]]
            [goog.events :as events]
            [goog.text.LoremIpsum]))

(enable-console-print!)

(defn add-message [state]
  (let [lorem (goog.text.LoremIpsum.)
        sentence (.generateSentence lorem)
        randroom (rand-nth (keys (get-active-rooms (:rooms @state))))
        randuser (rand-nth (get-in @state [:rooms randroom :users]))]

    (if (not (nil? randroom))
      (om/transact! state [:rooms randroom :msgs]
          (fn [msgs] (conj msgs {:sender randuser :content sentence :msg-type "text"})))
    ))
)

(def genrelist (list "acoustic" "ambient" "blues" "classical" "country" "electronic" "emo" "folk" "hardcore" "hip hop" "indie" "jazz" "latin" "metal" "pop" "pop punk" "punk" "reggae" "rnb" "rock" "soul" "world" "60s" "70s" "80s" "90s" ))

(defn usernames [] (vec (filter #(= (rand-int 2) 1) ["Ryoji Ikeda" "Richie Hawtin" "Nathan Fake" "Jimi Hendrix" "Alva Noto" "Speedy J" "Aphex Twin" "Mike Dehnert" "Luke Abbott" "John Coltrane" "Kangding Ray" "Electric Wizard" "Frédéric Chopin"])))

(def make-roomslist
  (apply hash-map
   (flatten (map (fn [r]
     (list r {:usercount (rand-int 133) :id (guid)}))
                 genrelist))))

(defn mock [state]
  (let [timer (goog/Timer. 10000)]
    (.start timer)
    (events/listen timer Timer/TICK #(add-message state))))
