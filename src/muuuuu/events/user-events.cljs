(ns muuuuu.events.user-events
  (:require [goog.events :as events]
            [muuuuu.controllers.files]
            [goog.events.FileDropHandler :as FileDropHandler]))

(defn up-and-down-keys "Bind panelsnap to up and down arrow keys." []
  ; still need to rewrite to goog.events
  (.add js/shortcut
    "Up"
    #(.panelSnap (js/$ ".chat") "snapTo" "prev")
    #js {:type "keydown" :propagate false :target js/document}
  )

  (.add js/shortcut
    "Down"
    #(.panelSnap (js/$ ".chat") "snapTo" "next")
    #js {:type "keydown" :propagate false :target js/document}
))

(defn rightkey-show-catalogue []
  ; still needs some work on 
  (.add js/shortcut
    "Right"
    #(.addClass (js/$ ".catalogue") "show")
    #js {:type "keydown" :propagate false :target js/document}
))

; when dropping files on window
(let [handler (events/FileDropHandler. js/document true)]
  (events/listen handler (-> events/FileDropHandler .-EventType .-DROP)
      muuuuu.controllers.files.drop-new-files))
