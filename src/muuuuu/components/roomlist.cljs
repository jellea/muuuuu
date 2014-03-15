(ns muuuuu.components.roomlist
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [guid get-next-color get-active-rooms]]
            [muuuuu.mock :refer [usernames]]))


; Structure (element.classname  - componentname)
; div.sidebar                   - allrooms
;   div.channellist
;     ul.joinchatmenu
;       li                      - channel
;     ul
;       li                      - addchannelform
;         form
;           span
;           input
;       li                      - channel

(defn add-channel
  "Puts a new channel in the app-state"
  [title rooms]
  (let [newroom      (if (= title "create new room") true)
        stnd-message (if-not newroom {:sender "muuuuu", :msg-type "action"
                                      :content (str "You just joined " title), :id (guid)})
        room         {:color (get-next-color) :active true :connected (if-not newroom true false)
                      :order (count (get-active-rooms @rooms))
                      :inviewport false :id (guid) :users (if-not newroom (usernames))
                      :msgs [stnd-message]}]

    (om/transact! rooms
      (fn [rooms] (assoc rooms title room)))))

(defn joinedchannel
  "Joined list item component"
  [data owner]
  (let [title (first data)
        room  (second data)
        color (:color room)]
  (om/component
    (html [:li
            {:data-panel title
             :className (str (if (false? (:bright color)) "bright") " "
                             (if (true? (:unread room)) "unread") " "
                             (if (true? (:inviewport room)) "active"))
             :style  #js {:borderColor (str "#" (:hex color))
                          :backgroundColor (str "#" (:hex color))}}
            [:a title]
          ]))))

(defn popularchannel
  "Popular list item component"
  [data owner opts]
  (om/component
      (html [:li {:onClick #(add-channel (first data) opts)}
              [:a (first data)
                [:span.count (:usercount (second data))]
            ]])))

(defn init
  "Room list sidebar component"
  [rooms owner]
  (om/component
    (html [:aside.sidebar
            [:div.channellist
              (if (> (count (get-active-rooms rooms)) 0)
                [:ul
                  [:li.header "Active rooms"
                    [:a {:onClick #(add-channel "create new room" rooms) :title "Create new room"} " +"]]])
              [:ul.joinchatmenu
                (om/build-all joinedchannel
                    (sort-by #(:order (second %)) (get-active-rooms rooms)) {:key :id})]
              [:ul
                [:li.header "Popular rooms"]]
              [:ul.popularchatmenu
                (om/build-all popularchannel
                    (sort-by #(:usercount (second %)) >
                             (filter #(not (true? (:active (second %)))) rooms))
                    {:key :id :opts rooms})
          ]]])))
