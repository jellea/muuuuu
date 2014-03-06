(ns muuuuu.components.catalogue
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.events.user-events :refer [rightkey-show-catalogue]]
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
  (reify
    om/IDidMount
    (did-mount [_]
      (rightkey-show-catalogue)
    )
    om/IRender
    (render [_]
      (html [:aside.catalogue
              [:h2 whos]
              [:div.releases
                (if mostlistened
                  [:h3 "most listened"]
                  (om/build-all release mostlistened {:key :id})
                )
              ]]))))
