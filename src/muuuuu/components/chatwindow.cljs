(ns muuuuu.components.chatwindow
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.utils :refer [get-active-rooms]]))

(enable-console-print!)

; Structure (element.classname - componentname)
; div.chat - allrooms
;   section.chatroom - room
;     h2
;     div.chatcontainer

; join leave events

(defn message [message]
  (om/component
    (dom/div nil "message")))

(defn messages [data owner {:keys [room] :as opts}]
  (om/component
    (dom/div nil "messages")))

(defn room [data owner opts]
  (let [[title color]
        [(first data) (:color (second data))]]
    (reify
      om/IDidMount
      (did-mount [_]
        ; jump to chatroom on mount
        (.panelSnap (js/$ ".chat") "snapToPanel"
          (js/$ (str "[data-panel=\"" title "\"]"))
        )
      )
      om/IRender
      (render [_]
        (dom/section
          #js {:data-panel title
               :className (str "chatroom"
                            (if (false? (:bright color)) " bright"))
               :style  #js {:backgroundColor (str "#" (:hex color))}}
              (dom/h2 nil title)
              (dom/div #js {:className "div.chatcontainer"}
                (om/build messages data {:opts {:room title}})))))))

(defn intro [app owner]
  (om/component
  (dom/div #js{:className "intro"}
             (dom/h3 nil "Hi, here's how to get started.")
             (dom/p #js{:className "joinchat"} "join chatrooms")
             (dom/div nil
               (dom/img #js {:src "resources/img/drag-example.png"})
               (dom/p nil "share music from your computer"))
             (dom/p #js{:className "listenmusic"} "listen music"))))

(defn init [rooms owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (.panelSnap (js/$ ".chat") #js {:$menu (js/$ ".joinchatmenu")
                              :slideSpeed 200
                              :menuSelector "li"})

      (.add js/shortcut
        "Up"
        #(.panelSnap (js/$ ".chat") "snapTo" "prev")
        #js {:type "keydown" :propagate false :target js/document}
      )

      (.add js/shortcut
        "Down"
        #(.panelSnap (js/$ ".chat") "snapTo" "next")
        #js {:type "keydown" :propagate false :target js/document}
      )

      (.on (js/$ ".chat") "panelsnap:start" (fn [self target]
        ;set focus on chat input after scroll
        (.focus (. js/document (getElementById "yourmsg")))

        ; Warning dirty hacking ahead!
        (om/transact! rooms
          (fn [rooms]
            ; flatten the lists to a big hash map
            (apply hash-map (flatten
              ; iterate and make lists like: (name {:viewport true})
              (map (fn [r]
                (list (first r) (merge (second r) {:inviewport
                  (if (= (first r) (.attr target "data-panel")) true false)
              })))
            rooms)))))
      ))
    )
    om/IRender
    (render [_]
      (apply dom/div #js {:className "chat"}
        (if (= (count (get-active-rooms rooms)) 0)
          (om/build intro nil)
          )
        (om/build-all room
          (sort-by #(:order(second %)) (get-active-rooms rooms))
          {:key :id})))
    ))
