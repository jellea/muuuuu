(ns muuuuu.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.components.chatwindow]
            [muuuuu.components.sidebarleft]
            [muuuuu.components.sidebarright]
            [muuuuu.components.chatinput]
            [muuuuu.utils :refer [guid get-next-color]]
            ))

(enable-console-print!)

(def app-state
  (atom
     {:yourname "your name"
      :rooms []
     :releases []}))

(defn container [app owner]
  (reify
  om/IWillMount
    (will-mount [_])
    om/IDidMount
    (did-mount [_]
      (muuuuu.components.sidebarleft.addChannel "Techno" app)
    )
    om/IRender
    ; if inviewport give class 'selected'
    (render [_]
      (dom/div #js {:className "container"}
           (om/build muuuuu.components.chatwindow.init app)
           (om/build muuuuu.components.sidebarright.init app)
           (om/build muuuuu.components.sidebarleft.init app)
           (om/build muuuuu.components.chatinput.init app)))))

(om/root
  container
  app-state
  {:target (. js/document (getElementById "app"))})
