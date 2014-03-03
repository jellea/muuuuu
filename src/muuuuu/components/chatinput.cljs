(ns muuuuu.components.chatinput
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [goog.events :as events]
            [goog.events.KeyHandler]
            [goog.events.KeyCodes]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [guid get-active-rooms current-room]]))

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

(defn send-message
  [e state roomname owner]
  (let [[text text-node] (value-from-node owner "yourmessage")]
    (when text
      (om/transact! state [:rooms roomname :msgs]
        (fn [msgs] (conj msgs {:sender "You" :content text :msg-type "self"})))
      (clear-nodes! text-node))
    false))

(defn init [state owner]
  (if (> (count (get-active-rooms (:rooms state))) 0)
  (let [current (current-room (:rooms state)) roomname (str (first current))]
    (reify
      om/IDidMount
      (did-mount [_]
        (let [keyboard (events.KeyHandler. (by-class "name"))]
          (event/listen keyboard
                        "key"
                        (fn [e] (when (= (.-keyCode e) events.KeyCodes/TAB)
                                 (do (prn "ENTEERRU!"))))))
      )
      om/IRender
      (render [_]
        (html [:div.chatinput
                [:form {:onSubmit #(send-message % state roomname owner)}
                  [:div {:className
                      (str "name" (if (:bright (:color (second current)) true) "" " bright"))}
                    [:a (:yourname state)]]
                  [:input#yourmsg.yourmessage {:type "text" :ref "yourmessage"
                                             :placeholder "Your Message"}]
                  [:input {:type "submit" :value "Send!"}]
              ]]))))
  (om/component
    (html [:div ""]))))
