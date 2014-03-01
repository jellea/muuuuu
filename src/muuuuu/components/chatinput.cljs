(ns muuuuu.components.chatinput
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
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

(defn handle-submit
  [e app owner]
  (let [[text text-node] (value-from-node owner "yourmessage")]
    (when text
      ;(om/transact! app
      ;(fn [rooms] (assoc rooms test {:test true})))
      ;(om/transact! app
        ;fn [rooms] (assoc :rooms {:sender "you" :content text})
      ;)
      (prn @app)
      (clear-nodes! text-node))
    false))

(defn init [app owner]
  (if (> (count (get-active-rooms (:rooms app))) 0)
  (om/component
    (html [:div.chatinput
            [:form {:onSubmit #(send-message % app owner)}
              [:div {:className
                  (str "name" (if (:bright (:color (second (first
                  (filter (fn [r] (= (:inviewport (second r)) true)) (:rooms app))
                                   ))) true) "" " bright"))}
                (:yourname app)]
              [:input#yourmsg.yourmessage {:type "text" :ref "yourmessage"
                                         :placeholder "Your Message"}]
              [:input {:type "submit" :value "Send!"}]
          ]]))
  (om/component
    (html [:div ""]))))
