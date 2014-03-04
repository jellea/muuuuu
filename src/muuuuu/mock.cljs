(ns muuuuu.mock
  (:require [goog.Timer :as Timer]
            [om.core :as om :include-macros true]
            [muuuuu.utils :refer [get-active-rooms]]
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

(defn mock [state]
  (let [timer (goog/Timer. 5000)]
    (.start timer)
    (events/listen timer Timer/TICK #(add-message state))))
