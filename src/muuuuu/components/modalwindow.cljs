(ns muuuuu.components.modalwindow
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn hide-modal [state]
  ; TODO sets state, but no update..
  (om/set-state! state :hidden true))

(defn modal [state owner]
  (om/component
    (html (if (false? (:hidden state))
          [:div.modal
            [:div.inner
              [:h3 "title"]
              [:p "text"]]
            [:div.mask {:onClick #(hide-modal owner)} ""]
           ] [:div]))))
