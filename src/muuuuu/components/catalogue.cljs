(ns muuuuu.components.catalogue
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.fx]
            [goog.fx.DragDrop]
            [goog.fx.DragDropGroup]
            [muuuuu.events.user_events :refer [rightkey-show-catalogue]]
            [sablono.core :as html :refer-macros [html]]
            ))

(def releases-dnd-group (goog.fx.DragDropGroup.))

(defn add-new-target [elemid elemgroup]
  (let [new-target (goog.fx.DragDrop. elemid)]
    (.init new-target)
    (.addTarget elemgroup new-target)
    (.init elemgroup)))

(defn init-drag-drop
  "Initiate drag and drop listeners group"
  []
    (add-new-target "chatinput" releases-dnd-group)
    (let [dropzone (.getElementById js/document "app")]
      (events/listen releases-dnd-group "dragstart" (fn [e]
        (.add (.-classList dropzone) "dropzone")
        (set! (-> e .-dragSourceItem .-element .-style .-opacity) 0.3)))
      (events/listen releases-dnd-group "dragover" (fn [e]
        (set! (-> e .-dropTargetItem .-element .-style .-outlineColor) "rgba(255,255,255,0.8)")))
      (events/listen releases-dnd-group "dragout" (fn [e]
        (set! (-> e .-dropTargetItem .-element .-style .-outlineColor) "rgba(255,255,255,0.3")))
      (events/listen releases-dnd-group "dragend" (fn [e]
        (.remove (.-classList dropzone) "dropzone")
        (set! (-> e .-dragSourceItem .-element .-style .-opacity) 1)))))

(defn release
  "Release component"
  [{:keys [img] :as app} owner {:keys [releasesgroup] :as opts}]
  (reify
    om/IDidMount
    (did-mount [_]
      ; add to dragdropgroup
      (.addItem releases-dnd-group (om/get-node owner "release") img)
    )
    om/IRender
    (render [_]
    (html [:div.release {:ref "release"}
            [:img {:src img}]]))))

(defn init
  "Library (right) sidebar component"
  [{:keys [whos mostlistened files] :as releases} owner]
    (reify
      om/IDidMount
      (did-mount [_]
        ;(rightkey-show-catalogue)
        (init-drag-drop))
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
                  ]]))))
