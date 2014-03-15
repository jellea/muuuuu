(ns muuuuu.controllers.notifications
  (:require [goog.Timer :as Timer]
            [goog.events :as events]))

(def state (atom {:title "" :message "" :time 5000}))

(defn on-mention [msg roomname]
  (if (re-find #"@Guest" (:content msg)) ; TODO correct regex
  ; TODO if window not in focus and or not in viewport
  (let [note (make-notification (str (:sender msg) " mentions you") (:content msg) 5000)]
    (set! (.-onclick note)
      #((.focus js/window nil)
        (.panelSnap (js/$ ".chat") "snapToPanel"
        (js/$ (str "[data-panel=\"" roomname "\"]")))
      )))))

(defn make-notification
  [title message duration]
  (let [notifi (.-webkitNotifications js/window)]
    (if (not= (.checkPermission notifi) 0)
      (.requestPermission notifi)
      (let [note (.createNotification notifi nil title message)]
          (.show note)
          (let [timer (goog/Timer. (or duration 2000))]
            (.start timer)
            (events/listen timer Timer/TICK #(.cancel note)))
        note))))

(add-watch state :notification
  (fn [_ _ _ message]
    (make-notification (:title @state) (:message @state) (:time @state))))
