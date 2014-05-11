(ns muuuuu.mock
  (:require [goog.Timer :as Timer]
            [om.core :as om :include-macros true]
            [muuuuu.utils :refer [get-active-rooms guid]]
            [goog.events :as events]
            [cljs.core.async :refer [<! chan timeout]]
            [goog.text.LoremIpsum])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn rand-action [state]
  (let [lorem     (goog.text.LoremIpsum.)
        sentence  (.generateSentence lorem)
        randroom  (rand-nth (keys (get-active-rooms (:rooms @state))))
        randuser  (rand-nth (get-in @state [:rooms randroom :users]))
        normalmsg {:sender randuser :content sentence :msg-type "text"}
        mention   {:sender randuser
                   :content (str "@" (:yourname @state) " are you paying attention?") :msg-type "text"}
        listening {:sender randuser
                   :content (str "is listening to " (first (usernames)) "- Track " (rand-int 22) "    Listen")
                   :msg-type "action"}
        msg       (rand-nth (into [listening mention] (for [i (range 5)] normalmsg)))]
    (if (and (not (nil? randroom)) (not (nil? randuser)))
      (om/transact! state [:rooms randroom :msgs]
          (fn [msgs] (conj msgs msg))))))

(def genrelist (list "acoustic" "ambient" "blues" "classical" "country" "electronic" "emo" "folk" "hardcore"
                     "hip hop" "indie" "jazz" "latin" "metal" "pop" "pop punk" "punk" "reggae" "rnb" "rock"
                     "soul" "world" "60s" "70s" "80s" "90s" ))

(defn usernames [] (vec (filter #(= (rand-int 2) 1) ["Ryoji Ikeda" "Porter Ricks" "Miles Davis" "Four Tet"
                                                     "Kevin Drumm" "Nathan Fake" "Jimi Hendrix" "Alva Noto"
                                                     "Speedy J" "Aphex Twin" "Mike Dehnert" "Luke Abbott"
                                                     "John Coltrane" "Oren Ambarchi" "William Basinski"
                                                     "Thom Yorke" "Anton Webern" "Kangding Ray"
                                                     "Electric Wizard" "Frédéric Chopin"])))

(def albumcovers (for [i (range 1 12)] {:img (str "/resources/img/covers/" i ".jpg") :id (guid)}))

(def make-roomslist
  (apply hash-map
   (flatten (map (fn [r]
     (list r {:usercount (rand-int 133) :id (guid)}))
                 genrelist))))

(defn mock [state]
  (go
    (while true
      (<! (timeout (rand-int 2000 5000)))
      (rand-action state))))

  ;(let [timer (goog/Timer. 2500)]
    ;(.start timer)
    ;(events/listen timer Timer/TICK #(rand-action state))))
