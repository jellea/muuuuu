(ns muuuuu.components.sidebarright
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            ))

; Structure (element.classname - componentname)
; div.catalogue   - musiclist
;   h2
;   div.releases
;     div.release - release

(defn release [{:keys [img id]} app owner]
  (om/component
    (dom/div #js {:className "release"}
      (dom/img #js {:src img}))))

(defn init [{:keys [releases]}]
  (om/component
    (dom/div #js {:className "catalogue"}
      (dom/h2 nil "Your Library")
      (apply dom/div #js {:className "releases"}
        (om/build-all release releases {:key :id})))))
