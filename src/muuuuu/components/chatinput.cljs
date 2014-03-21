(ns muuuuu.components.chatinput
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [goog.events :as events]
            [sablono.core :as html :refer-macros [html]]
            [muuuuu.utils :refer [guid get-active-rooms current-room]]))

(defn- value-from-node
  [component field]
  (let [n (om/get-node component field)
        v (-> n .-value clojure.string/trim)]
    (when-not (empty? v)
      [v n])))

(defn- clear-nodes!
  [& nodes]
  (doall (map #(set! (.-value %) "") nodes)))

(defn change-name
  "Change user name"
  [e owner state roomname]
  (let [elem (.-target e)
        prevname (.-innerText elem)]
    (when (= (.-keyCode e) 13); ENTER key
      (do (.preventDefault e)
          (.blur elem)
          (om/transact! state
            #(-> %
               (assoc :yourname (.-innerText elem))
               (update-in [:rooms roomname :msgs]
                 conj {:sender "muuuuu" :content (str "You changed your name to " (.-innerText elem)) :msg-type "action"})))
          false))))

(defn send-message
  [e state roomname owner]
  (let [[text text-node] (value-from-node owner "yourmessage")]
    (when text
      (om/transact! state [:rooms roomname :msgs]
        (fn [msgs] (conj msgs {:sender "You" :content text :msg-type "self"})))
      (clear-nodes! text-node))
    false))

(defn init
  "Chatroom input field component"
  [state owner]
  (let [current (current-room (:rooms state)) roomname (str (first current))]
    (reify
      om/IRender
      (render [_]
        (html [:form#chatinput
                (if (= (count (get-active-rooms (:rooms state))) 0)
                  {:style {:display "none"}}
                  {:onSubmit #(send-message % state roomname owner)})
                [:div {:className
                    (str "name" (if (:bright (:color (second current)) true) "" " bright"))}
                  [:a#yourname {:onKeyDown #(change-name % owner state roomname)
                                :contentEditable true :ref "yourname"} (:yourname state)]]
                [:input#yourmsg.yourmessage {:type "text" :ref "yourmessage"
                                           :placeholder "Your Message"}]
                [:input {:type "submit" :value "Send!"}]
              ])))))
