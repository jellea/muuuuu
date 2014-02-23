(ns muuuuu.components.sidebarright
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            ))

; Structure (element.classname - componentname)
; div.catalogue   - musiclist
;   h2
;   div.releases
;     div.release - release

(defn release [{:keys [img id]} app owner]
  (om/component
    (html [:div.release
            [:img {:src img}]])))

(defn init [releases owner]
  (om/component
    (html [:div.catalogue
            [:h2 "Your Library"]
            [:div.releases
              (om/build-all release releases {:key :id})]])))
