(ns muuuuu.components.sidebarleft
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.utils :refer [guid]]))

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
  (let [[room] [{:title title :color {:hex "ff0000" :bright false} :id (guid)}]]
    (om/transact! app [:rooms]
      (fn [rooms] (conj rooms room))))
)

(defn newChannel
  "Handles form submit"
  [e owner app]
  (let [[channelname channel-node] (value-from-node owner "channelname")]
    (when channelname
      (addChannel channelname app)
      (clear-nodes! channel-node))
    false))

(defn channel [{:keys [title color]} app owner]
  (om/component
    (dom/li
      #js {:data-panel title
           :className (if (false? (:bright color)) "bright")
           :style  #js {:borderColor (str "#" (:hex color))
                        :backgroundColor (str "#" (:hex color))}}
      (dom/a nil title))))

(defn addchannelform [app owner]
  (om/component
    (dom/li #js {:className "addchannel"}
      (dom/form  #js {:onSubmit #(newChannel % owner app)}
        (dom/span nil "Add new")
        (dom/input #js {:type "text" :placeholder "channel name" :ref "channelname"})))))

(defn init [app owner]
  (om/component
   (dom/div #js {:className "sidebar"}
      (dom/div #js {:className "channellist"}
         (apply dom/ul #js {:className "joinchatmenu"}
                (om/build-all channel (:rooms app) {:key :id}))
         (dom/ul nil 
                 (om/build addchannelform app)
                 ;(om/build-all channel (:rooms app) {:key :id})
                 )))))
