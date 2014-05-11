(ns muuuuu.garbagecollector
  (:require [om.core :as om :include-macros true]
            [cljs.core.async :refer [<! chan timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn prunemsgs
  "Limits the number of messages in rooms on the state to nummsgs"
  [state nummsgs]
  ; TODO maintain sort
  (do
    (om/transact! state [:rooms]
                 (fn [rooms]
                   (apply hash-map (flatten
                     (map
                       (fn [room] (list (room 0)
                                        (update-in (room 1) [:msgs] #(take nummsgs %))))
                       rooms)))))
    ))

;(defn interval [state]
  ;(go
    ;(while true
      ;(<! (timeout 10000))
      ;(prunemsgs state 20))))
