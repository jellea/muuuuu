(ns muuuuu.components.catalogue
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.fx :refer [DragDrop DragDropGroup]]
            [goog.fx.DragDrop]
            [goog.fx.DragDropGroup]
            [muuuuu.events.user-events :refer [rightkey-show-catalogue]]
            [sablono.core :as html :refer-macros [html]]
            ))

(defn drag-drop
  "Initiate drag and drop listeners group"
  [releasesgroup]
  (let [target (goog.fx.DragDrop. "chat")]
    (.init target)
    (.addTarget releasesgroup target)
  )
  (.init releasesgroup)

  (events/listen releasesgroup "dragstart"
      (fn [e] (prn "dragstart")
              ; set .chatmessages and chatinput to red
              ; make placeholder grey
              (.log js/console (-> e .-dragSourceItem .-element))
        ))

  (events/listen releasesgroup "drag"
      (fn [e] (prn "dragover")
              ; set .chatmessages to darker red
        ))

  ;'dragover'
  ;'dragout'
  ;'drag' > .chatmessage and input naar rood + 
  ;'drop'
  ;'dragstart'
  ;'dragend'
)

(defn release
  "Release component"
  [{:keys [img] :as app} owner {:keys [releasesgroup] :as opts}]
  (reify
    om/IDidMount
    (did-mount [_]
      (.addItem releasesgroup (om/get-node owner "release") img)
    )
    om/IRender
    (render [_]
    (html [:div.release {:ref "release"}
            [:img {:src img}]]))))


(defn init
  "Library (right) sidebar component"
  [{:keys [whos mostlistened] :as releases} owner]
  (let [releasesgroup (goog.fx.DragDropGroup.)]
    (reify
      om/IDidMount
      (did-mount [_]
        (rightkey-show-catalogue)
        (drag-drop releasesgroup)
      )
      om/IDidUpdate
      (did-update [_ _ _]
        (.log js/console drag)
      )
      om/IRender
      (render [_]
          (html [:aside.catalogue
                  [:h2 whos]
                  [:div.releases
                    (if (not= (count mostlistened) 0) [:h3 "most listened"])
                    (om/build-all release (take 8 mostlistened)
                                  {:key :id :opts {:releasesgroup releasesgroup}})
                    (if (not= (count files) 0)
                      [:h3 "folders"])
                  ]])))))
