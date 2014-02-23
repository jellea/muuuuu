(ns muuuuu.components.room-management
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [muuuuu.utils :refer [guid get-next-color get-active-rooms]]))

(enable-console-print!)

(defn- value-from-node
  [component field]
  (let [n (om/get-node component field)
        v (-> n .-value clojure.string/trim)]
    (when-not (empty? v)
      [v n])))

(defn- clear-nodes!
  [& nodes]
  (doall (map #(set! (.-value %) "") nodes)))

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

(defn addChannel [title rooms]
  "Puts a new channel in the app-state"
  (let [[room] [{:color (get-next-color) :active true
                 :order (count (get-active-rooms @rooms))
                 :inviewport false :id (guid)
                 :msgs ()}]]
    (om/transact! rooms
      (fn [rooms] (assoc rooms title room)))))

(defn newChannel
  "Handles form submit"
  [e app owner]
  (let [[channelname channel-node] (value-from-node owner "channelname")]
    (when channelname
      (addChannel channelname app)
      (clear-nodes! channel-node))
    false))

(defn joinedchannel [data owner]
  (let [[title color][(first data)(:color (second data))]]
  (om/component
    (dom/li
      #js {:data-panel title
           :className (if (false? (:bright color)) "bright")
           :style  #js {:borderColor (str "#" (:hex color))
                        :backgroundColor (str "#" (:hex color))}}
      (dom/a nil title)))))

(defn popularchannel [data owner opts]
  (om/component
      (dom/li #js {:onClick #(addChannel (first data) opts)}
        (dom/a nil
          (first data)
          (dom/span #js {:className "count"} (:users (second data)))
          ))))

(defn addchannelform [app owner]
  (om/component
    (dom/li #js {:className "addchannel"}
      (dom/form  #js {:onSubmit #(newChannel % app owner)}
        (dom/span nil "Create new room")
        (dom/input #js {:type "text" :placeholder "Create new room" :ref "channelname"})))))


(defn init [rooms owner]
  (om/component
   (dom/div #js {:className "sidebar"}
      (dom/div #js {:className "channellist"}
         (apply dom/ul #js {:className "joinchatmenu"}
                (om/build-all joinedchannel (sort-by #(:order(second %)) (get-active-rooms rooms)) {:key :id}))
         (apply dom/ul #js {:className "popularchatmenu"}
                 (om/build addchannelform rooms)
                 (om/build-all popularchannel (sort-by #(:users(second %)) > (filter #(not (true? (:active (second %)))) rooms)) {:key :id :opts rooms}))))))
