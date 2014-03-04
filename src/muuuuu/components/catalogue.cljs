(ns muuuuu.components.catalogue
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

(defn init [{:keys [whos mostlistened] :as releases} owner]
  (om/component
    (html [:aside.catalogue
            [:h2 whos]
            [:div.releases
              (if mostlistened
                [:h3 "most listened"]
                (om/build-all release mostlistened {:key :id})
              )
            ]])))
