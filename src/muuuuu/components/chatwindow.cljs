(ns muuuuu.components.chatwindow
  (:require [goog.events :as events]
            [goog.events.KeyHandler]
            [clojure.set :refer [rename-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.events.user-events]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [get-active-rooms current-room]]))


; Structure (element.classname - componentname)
; div.chat - allrooms
;   section.chatroom - room
;     h2
;     div.chatcontainer

; join leave events

(defn message [{:keys [sender msg-type content] :as msg} owner]
  (om/component
    (html [:div {:className (str "message " msg-type)}
            [:div.sender
              [:a (if (not= sender "You"){:onClick #(click % sender owner)})
               sender]]
            [:div.content content]
          ])))

(defn messages [data owner {:keys [roomstate]}]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      ; scroll messages to the bottom
      ; TODO if scrolled up, don't scroll down
      (set! (.-scrollTop (.getDOMNode owner)) 1000000)
      ;(prn (type roomstate))
      ; if current room is not :intheviewport set :unread true
      ;(if (false? (:inviewport (second roomstate)))
        ;(prn roomstate)
        ;(om/set-state! roomstate (first roomstate) (assoc (second roomstate) (:unread true))))
    )
    om/IRender
    (render [_]
      (html [:div.messages
        (om/build-all message data {:key :id})]))
))

(defn user [user owner]
  (om/component
    (html [:li {:onClick #(show-lib % user owner)} user])))

(defn delete-room [room state]
  (om/transact! state #(assoc-in %1 [room :active] false)))

(defn rename-room [e title state]
  (om/transact! state
    #(rename-keys %1 {"create new room" title})))

(defn room [data owner {:keys [state] :as opts}]
  (let [[title color msgs users]
        [(first data) (:color (second data)) (:msgs (second data)) (:users (second data))]]
    (reify
      om/IDidMount
      (did-mount [_]
        ; jump to chatroom on mount
        (.panelSnap (js/$ ".chat") "snapToPanel"
          (js/$ (str "[data-panel=\"" title "\"]"))
        )

        (if (= title "create new room")
          (let [htmlelement (om/get-node owner "title")]
            (.focus htmlelement)
            (let [handler (events/KeyHandler. htmlelement)]
              (events/listen handler
                "key" (fn [e] (when (= (.-keyCode e) 13); ENTER key
                  (do (.preventDefault e)
                    (if (not= (.-innerText htmlelement) "Create New Room")
                      (rename-room e (.-innerText htmlelement) state)) false))))
          ))
        )
      )
      om/IRender
      (render [_]
        (html [:section.chatroom {:data-panel title
                  :class (if (false? (:bright color)) "bright")
                  :style #js {:backgroundColor (str "#" (:hex color))}}
                [:h2
                  [:span.title
                    {:contentEditable (if (= title "create new room") true)
                     :ref "title"}
                    title]
                  [:span.options
                    [:a {:onClick #(delete-room title state)} "delete"]
                    [:a "notifications"] [:a "color"] [:a "backlog"]]]
                [:div.chatcontainer
                  (om/build messages (reverse (take 15 (reverse msgs))))
                  (if (:inviewport (second data))
                    [:ul.userlist
                      [:li.header "Users"
                        [:span.count (str" (" (count users) ")")]]
                      (om/build-all user users)])
                 ]
              ])))))

(defn intro [app owner]
  (om/component
  (html [:div.intro
          [:h3 "Hi, here's how to get started."]
          [:p.joinchat "join some chatrooms"]
          [:div
            [:img {:src "resources/img/drag-example.png"}]
            [:p "share music from your computer"]]
          [:p.listenmusic "listen music"]])))

(defn init [rooms owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (.panelSnap (js/$ ".chat")
                  #js {:$menu (js/$ ".joinchatmenu")
                       :slideSpeed 150
                       :menuSelector "li"})

      (muuuuu.events.user-events.up-and-down-keys)

      (.on (js/$ ".chat") "panelsnap:start" (fn [self target]
        ;(if (not (identical? (aget (.-prevObject target) "0") js/document))
          ;(.log js/console (.attr (.-prevObject target) "data-panel"))
        ;)

        ; skip focus to chatinput and focus title if new room
        (if (not= "new room" (.attr target "data-panel"))
          (.focus (. js/document (getElementById "yourmsg")))
        )

        (om/transact! rooms
          (fn [rooms]
            (let [current (.attr target "data-panel")
                  prev (first (current-room rooms))]
            (-> rooms
              (assoc-in [prev :inviewport] false)
              (assoc-in [current :inviewport] true))
            )))))
    )
    om/IRender
    (render [_]
      (html [:div.chat
              (if (= (count (get-active-rooms rooms)) 0)
                (om/build intro nil)
                )
              (om/build-all room
                (sort-by #(:order (second %)) (get-active-rooms rooms))
                {:key :id :opts {:state rooms}})
            ]))))
