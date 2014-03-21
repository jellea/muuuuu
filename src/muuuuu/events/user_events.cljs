(ns muuuuu.events.user_events
  (:require [goog.events :as events]
            [muuuuu.controllers.files]
            [goog.events.KeyHandler]
            [muuuuu.components.roomlist]
            [goog.history.Html5History]
            [goog.events.KeyCodes :as KeyCodes]
            [goog.events.FileDropHandler :as FileDropHandler]
            [muuuuu.utils :refer [get-active-rooms]]))

; Location

(def history (goog.history/Html5History.))

(defn init-history [state]
  (events/listen history (-> goog/history .-EventType .-NAVIGATE)
     (fn [e] 
       (let [roomname (.-token e)]
         (if (.-isNavigation e)
           (if (get-in @state [:rooms roomname :active])
               (.panelSnap (js/$ ".chat") "snapToPanel" (js/$ (str "[data-panel=\"" roomname "\"]"))))
           (muuuuu.components.roomlist/add-channel roomname (:rooms state))
         ))))
   (.setUseFragment history true)
   (.setEnabled history true))

; KeyBoard

(defn up-and-down-keys "Bind panelsnap to up and down arrow keys." []
  (let [handler (events/KeyHandler. js/document)]
    (events/listen handler
      "key" (fn [e]
        (when (= (.-keyCode e) 38); Up
          (do (.preventDefault e)
            (.panelSnap (js/$ ".chat") "snapTo" "prev")
            false))
        (when (= (.-keyCode e) 40); Down
          (do (.preventDefault e)
            (.panelSnap (js/$ ".chat") "snapTo" "next")
            false))))))

(defn rightkey-show-catalogue []
  ; TODO needs reset if hovered somewhere else
  (let [handler (events/KeyHandler. js/document)]
    (events/listen handler
      "key" (fn [e]
        (when (= (.-keyCode e) 39); Right
          (do (.preventDefault e)
            (.addClass (js/$ ".catalogue") "show")
            false))))))

(defn file-drop []
  (let [handler (events/FileDropHandler. js/document true)]
    (events/listen handler (-> events/FileDropHandler .-EventType .-DROP)
        muuuuu.controllers.files.drop-new-files)))
