(ns muuuuu.components.chatwindow
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

; Structure (element.classname - componentname)
; div.chat - allrooms
;   section.chatroom - room
;     h2
;     div.chatcontainer

; join leave events

(defn messages [data owner {:keys [room] :as opts}]
  (om/component
    (dom/div nil "messages")))

(defn room [{:keys [id title color inviewport] :as c} owner opts]
  (reify
    om/IWillMount
    (will-mount [_])
    om/IDidMount
    (did-mount [_ _]
      ; jump to chatroom on mount
      ;(.panelSnap (js/$ ".chat") "snapToPanel"
                  ;(js/$ (str "[data-panel=" title "]")))
      (prn (str title " mounted")))
    om/IRender
    ; if inviewport give class 'selected'
    (render [_]
      (dom/section
        #js {:data-panel title
             :className (str "chatroom" (if (false? (:bright color)) " bright"))
             :style  #js {:backgroundColor (str "#" (:hex color))}}
            (dom/h2 nil title)
            (dom/div #js {:className "div.chatcontainer"}
              (om/build messages c {:opts {:room title}}))))))

(defn init [app owner]
  (reify
    om/IDidMount
    (did-mount [_ _]
      (.panelSnap (js/$ ".chat") #js {:$menu (js/$ ".joinchatmenu")
                                      :slideSpeed 120
                                      :menuSelection "li"})

      ;(.on (js/$ ".chat") "panelsnap:start" (fn [self, target]
        ;; - iterate through excisting rooms and set inviewport to false if
        ;; title doesnt match
        ;; - push list
        ;(def title (.attr target "data-panel"))

        ;(om/transact! app [:rooms]
          ;(fn [rooms]
            ;(map
              ;(fn [a]
                ;(if (= (:title a) title)
                  ;(merge a {:inviewport true})
                  ;(merge a {:inviewport false})))
              ;rooms)
          ;))))
    )
    om/IRender
    (render [_]
      (apply dom/div #js {:className "chat"}
        (om/build-all room (:rooms app) {:key :id})))))
