(ns muuuuu.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.mock]
            [muuuuu.components.chatwindow]
            [muuuuu.components.roomlist]
            [muuuuu.components.catalogue]
            [muuuuu.components.chatinput]
            [muuuuu.components.musicplayer]
            [muuuuu.events.user_events]
            [goog.debug :as debug]
            [goog.debug.FpsDisplay]
            [muuuuu.components.modalwindow]
            [muuuuu.utils :refer [guid get-next-color]]
            ))

(enable-console-print!)


;(let [fpsDisplay (debug/FpsDisplay.)]
  ;(.render fpsDisplay (. js/document (getElementById "fpsdisplay"))))

(def app-state
  (atom {:yourname (str "Guest" (rand-int 9999))
         :yourlib []
         :rooms muuuuu.mock.make-roomslist
         :player {:tracknumber 11
                  :tracktitle "Treblinka"
                  :artist "Pig Destroyer"
                  :album "Prowler on the streets"}
         :modal {:hidden true}
         :catalogue {:whos "Your Library"
                     :mostlistened muuuuu.mock.albumcovers
                     :files [nil]
                    }}))

(defn container [state owner]
  (reify
    om/IWillMount
    (will-mount [_])
    om/IDidMount
    (did-mount [_]
      ; start mocking data
      (muuuuu.mock.mock state)
      ;(muuuuu.events.user_events.init-history app-state)
    )
    om/IRender
    (render [_]
      (dom/div #js {:className "container"}
        (om/build muuuuu.components.chatwindow.init (:rooms state))
        (om/build muuuuu.components.catalogue.init (:catalogue state))
        (om/build muuuuu.components.roomlist.init (:rooms state))
        (om/build muuuuu.components.chatinput.init state)
        (om/build muuuuu.components.modalwindow.modal (:modal state))
        (om/build muuuuu.components.musicplayer.init (:player state))))))

(om/root
  container
  app-state
  {:target (. js/document (getElementById "app"))})
