(ns muuuuu.components.roomlist
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [guid usernames get-next-color get-active-rooms]]))

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
                 :inviewport false :id (guid) :users usernames
                 :msgs [{:sender "muuuuu", :msg-type "action" :content (str "You just joined " title), :id (guid)}]}]]

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
    (html [:li
            {:data-panel title
             :className (if (false? (:bright color)) "bright")
             :style  #js {:borderColor (str "#" (:hex color))
                        :backgroundColor (str "#" (:hex color))}}
            [:a title]
          ]))))

(defn popularchannel [data owner opts]
  (om/component
      (html [:li {:onClick #(addChannel (first data) opts)}
              [:a
                (first data)
                [:span.count (:usercount (second data))]
            ]])))

(defn addchannelform [app owner]
  (om/component
    (html [:li.addchannel
            [:form {:onSubmit #(newChannel % app owner)}
              [:span "Create new room"]
              [:input {:type "text" :placeholder "Create new room"
                       :ref "channelname"}
          ]]])))

(defn init [rooms owner]
  (om/component
    (html [:aside.sidebar
            [:div.channellist
              (if (> (count (get-active-rooms rooms)) 0)
                [:ul
                  [:li.header "Active rooms" 
                    [:a {:title "Create new room"} " +"]]])
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
