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

(defn add-channel [title rooms]
  "Puts a new channel in the app-state"
  (let [[room] [{:color (get-next-color) :active true
                 :order (count (get-active-rooms @rooms))
                 :inviewport false :id (guid) :users (usernames)
                 :msgs [{:sender "muuuuu", :msg-type "action" :content (str "You just joined " title), :id (guid)}]}]]

    (om/transact! rooms
      (fn [rooms] (assoc rooms title room)))))

(defn joinedchannel [data owner]
  (let [title (first data)
        color (:color (second data))
        unread (:unread (second data))]
  (om/component
    (html [:li
            {:data-panel title
             :className (str (if (false? (:bright color)) "bright") ""
                        (if (true? unread) "unread"))
             :style  #js {:borderColor (str "#" (:hex color))
                        :backgroundColor (str "#" (:hex color))}}
            [:a title]
          ]))))

(defn popularchannel [data owner opts]
  (om/component
      (html [:li {:onClick #(add-channel (first data) opts)}
              [:a
                (first data)
                [:span.count (:usercount (second data))]
            ]])))

(defn init [rooms owner]
  (om/component
    (html [:aside.sidebar
            [:div.channellist
              (if (> (count (get-active-rooms rooms)) 0)
                [:ul
                  [:li.header "Active rooms"
                    [:a {:onClick #(add-channel "create new room" rooms) :title "Create new room"} " +"]]])
              [:ul.joinchatmenu
                (om/build-all joinedchannel
                    (sort-by #(:order (second %)) (get-active-rooms rooms))
                    {:key :id})]
              [:ul
                [:li.header "Popular rooms"]]
              [:ul.popularchatmenu
                ;(om/build addchannelform rooms)
                (om/build-all popularchannel
                    (sort-by #(:usercount (second %)) >
                        (filter #(not (true? (:active (second %)))) rooms))
                    {:key :id :opts rooms})
          ]]])))
