(ns muuuuu.controllers.notifications
  (:require [goog.Timer :as Timer]
            [goog.events :as events]))

(def state (atom {:title "" :message "" :time 5000}))

(defn on-mention [msg roomname]
  (if (re-find #"@Guest" (:content msg)) ; TODO correct regex
    ; TODO if window not in focus and or not in viewport
  (let [notifi (.-webkitNotifications js/window)
        note (.createNotification notifi nil (str (:sender msg) " mentions you") (:content msg))]
    (.show note)
    (let [timer (goog/Timer. (or (:time @state) 4000))]
      (.start timer)
      (set! (.-onclick note)
        #((.focus js/window nil)
          (.panelSnap (js/$ ".chat") "snapToPanel"
          (js/$ (str "[data-panel=\"" roomname "\"]")))
        ))
      (events/listen timer Timer/TICK #(.cancel note))
    ))
  ))


  ;(add-watch room-state :msgs
    ;(fn [_ _ _ msgs]
      ;(prn msgs)
      ;;(let [note (.createNotification notifi nil title message)]
          ;;(.show note)
          ;;(let [timer (goog/Timer. (or (:time @state) 1000))]
            ;;(.start timer)
            ;;(events/listen timer Timer/TICK #(.cancel note))
      ;;))
      ;)))

(add-watch state :notification
  (fn [_ _ _ message]
    (let [notifi (.-webkitNotifications js/window)
          title (:title @state) message (:message @state)]
    (if (not= (.checkPermission notifi) 0)
      (.requestPermission notifi)
      (let [note (.createNotification notifi nil title message)]
          (.show note)
          (let [timer (goog/Timer. (or (:time @state) 2000))]
            (.start timer)
            (events/listen timer Timer/TICK #(.cancel note))
          )
        )))))

; When a torrent completes, try to inform the user
; (dispatch/react-to #{:completed-torrent} (fn [_ torrent]
;   ; If the user is currently on the site don't show the notification
;   (if-not (.hasFocus js/document)
;     (.createNotification webkit-notifications
;       nil 
;       "notification title"
;       "notification content"
;       ))))
