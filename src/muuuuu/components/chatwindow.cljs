(ns muuuuu.components.chatwindow
  (:require [goog.events :as events]
            [goog.events.KeyHandler]
            [goog.History]
            [clojure.set :refer [rename-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.events.user-events]
            [clojure.string :refer [trim]]
            [muuuuu.controllers.notifications :as notify]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [get-active-rooms current-room get-next-color]]))


; Structure (element.classname - componentname)
; div.chat - allrooms
;   section.chatroom - room
;     h2
;     div.chatcontainer

; join leave events

(def history (goog/History.))

(defn show-lib [e user owner]
  (.log js/console user)
  ; Switch to different library
  (.text (js/$ ".catalogue h2") user)
  (.addClass (js/$ ".catalogue") "show")
  (.setTimeout js/window #(.removeClass (js/$ ".catalogue") "show"), 1000)
)

(defn message [{:keys [sender msg-type content] :as msg} owner opts]
  (reify
    om/IDidMount
    (did-mount [_]
      (notify/on-mention msg (:roomname opts))
    )
    om/IRender
    (render [_]
      (html [:div {:className (str "message " msg-type)}
              [:div.sender
                [:a (if (not= sender "You"){:onClick #(show-lib % sender owner)})
                 sender]]
              [:div.content content]
          ]))))

(defn messages [data owner {:keys [roomname state]}]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      ; scroll messages to the bottom
      ; TODO if scrolled up, don't scroll down
      (set! (.-scrollTop (.getDOMNode owner)) 1000000)
      ;(.log js/console owner)
    )
    om/IRender
    (render [_]
      (html [:div.messages
        (if-not (= data [nil]) ; prevents a new room from showing a stripe
          (om/build-all message data {:key :id :opts {:roomname roomname}})
          nil)]))
))

(defn user [user owner]
  (om/component
    (html [:li {:onClick #(show-lib % user owner)} user])))

(defn delete-room [room state]
  (om/transact! state #(assoc-in %1 [room :active] false)))

(defn rename-room [e title state]
  (.setToken muuuuu.events.user-events/history title)

  ; TODO bug - panelsnap breaks

  (om/transact! state
    #(rename-keys %1 {"create new room" title})))

(defn toggle-notifications [e title state]
  (reset! notify/state {:title "muuuuu" :message "Notifications are turned on"}))

(defn change-color [room state]
  (om/transact! state #(assoc-in %1 [room :color] (get-next-color))))

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
                "key" (fn [e]
                  (when (= (.-innerText htmlelement) "Create New Room")
                    (do (set! (.-innerText htmlelement) "")))
                  (when (= (.-keyCode e) 13); ENTER key
                    (do (.preventDefault e)
                      (if (not= (.-innerText htmlelement) "Create New Room")
                        (rename-room e (trim (.-innerText htmlelement)) state)) false))))))
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
                    [:a {:onClick #(toggle-notifications % title)} "notifications"]
                    [:a {:onClick #(change-color title state)} "color"]
                    [:a "backlog"]]]
                [:div.chatcontainer
                  (om/build messages (reverse (take 15 (reverse msgs))) {:opts {:state state :roomname title}})
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
                       :slideSpeed 200
                       :menuSelector "li"})

      (muuuuu.events.user-events.up-and-down-keys)

      (.on (js/$ ".chat") "panelsnap:start" (fn [self target]
        (let [roomtitle (.attr target "data-panel")]
          ;(if (not (identical? (aget (.-prevObject target) "0") js/document))
            ;(.log js/console (.attr (.-prevObject target) "data-panel"))
          ;)

          ; skip focus to chatinput and focus title if new room
          (if (not= "create new room" roomtitle)
            (.focus (. js/document (getElementById "yourmsg")))
          )

          (.setToken muuuuu.events.user-events/history roomtitle)

          (om/transact! rooms
            (fn [rooms]
              (let [prev (first (current-room rooms))]
              (-> rooms
                (assoc-in [prev :inviewport] false)
                (assoc-in [roomtitle :inviewport] true))
              )))))
    ))
    om/IRender
    (render [_]
      (html [:div#chat.chat
              (if (= (count (get-active-rooms rooms)) 0)
                (om/build intro nil)
                )
              (om/build-all room
                (sort-by #(:order (second %)) (get-active-rooms rooms))
                {:key :id :opts {:state rooms}})
            ]))))
