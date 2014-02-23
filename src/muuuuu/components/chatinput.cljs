(ns muuuuu.components.chatinput
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [muuuuu.utils :refer [guid get-active-rooms]]))

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

; Structure (element.classname - componentname)
; div.chat - allrooms
;   section.chatroom - room
;     h2
;     div.chatcontainer

;(defn save-comment!
  ;[comment url]
  ;(go (let [res (<! (http/post url {:json-params comment}))]
        ;(prn (get-in res [:body :message])))))

(defn handle-submit
  [e owner]
  (let [[text text-node] (value-from-node owner "yourmessage")]
    (when text
      (clear-nodes! text-node))
    false))

(defn init [app owner]
  (if (> (count (get-active-rooms (:rooms app))) 0)
  (om/component
      (dom/div #js {:className "chatinput"}
        (dom/form #js {:onSubmit #(handle-submit % owner)}
          (dom/div #js {:className (str "name"
            (if (:bright (:color (second (first
               (filter (fn [r] (= (:inviewport (second r)) true)) (:rooms app))
                                   ))) true) "" " bright"))}
            (:yourname app))
          (dom/input #js {:id "yourmsg" :className "yourmessage" :type "text" :ref "yourmessage" :placeholder "Your Message"})
          (dom/input #js {:type "submit" :value "Send!"}))))
  (om/component
      (dom/div nil nil))))
