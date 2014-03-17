(ns muuuuu.components.modalwindow
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn hide-modal [state]
  (om/transact! state #(assoc % :hidden true)))

(defn modal
  "Modal component"
  [state owner]
  (om/component
    (html (if (false? (:hidden state))
          [:div.modal
            [:div.inner
             (if (:component state)
               (om/build (:component state) nil)
               (html [:h3 "title"][:p "text"]))]
            [:div.mask {:onClick #(hide-modal state)} ""]
           ] [:div]))))
