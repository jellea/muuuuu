(ns muuuuu.components.sidebarleft
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [muuuuu.utils :refer [guid get-next-color]]))

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

(defn addChannel [title app]
  "Puts a new channel in the app-state"
  (let [[room] [{:color (get-next-color) :active true :inviewport false :id (guid)}]]
    (om/transact! app [:rooms]
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

(defn popularchannels [data owner opts]
  (om/component
      (dom/li nil
        (dom/a #js {:onClick #(addChannel (first data) opts)}
          (first data)
          ;(dom/span #js {:className "count"} 3)
          ))))

(defn addchannelform [app owner]
  (om/component
    (dom/li #js {:className "addchannel"}
      (dom/form  #js {:onSubmit #(newChannel % app owner)}
        (dom/span nil "Add new")
        (dom/input #js {:type "text" :placeholder "channel name" :ref "channelname"})))))


(defn init [{:keys [rooms] :as app} owner]
  (om/component
   (dom/div #js {:className "sidebar"}
      (dom/div #js {:className "channellist"}
         (apply dom/ul #js {:className "joinchatmenu"}
                (om/build-all joinedchannel (filter #(true? (:active (second %))) rooms) {:key :id}))
         (apply dom/ul nil
                 (om/build addchannelform app)
                 (om/build-all popularchannels (filter #(not (true? (:active (second %)))) rooms) {:key :id :opts app}))))))
