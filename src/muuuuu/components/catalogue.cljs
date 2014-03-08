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

(defn release [{:keys [img]} app owner]
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
                (if (not= (count mostlistened) 0) [:h3 "most listened"])
                (om/build-all release (take 6 mostlistened) {:key :id})
                (if (not= (count files) 0) [:h3 "files"])
              ]]))))
