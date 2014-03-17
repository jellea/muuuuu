(ns muuuuu.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.mock]
            [muuuuu.dispatch :as dispatch]
            [muuuuu.components.chatwindow :as chatwindow]
            [muuuuu.components.roomlist :as roomlist]
            [muuuuu.components.catalogue :as catalogue]
            [muuuuu.components.chatinput :as chatinput]
            [muuuuu.components.musicplayer :as musicplayer]
            [muuuuu.components.modalwindow :as modalwindow]
            [muuuuu.events.user_events]
            [goog.debug :as debug]
            [goog.debug.FpsDisplay]
            [muuuuu.utils :refer [guid get-next-color]]
            ))

(enable-console-print!)


;(let [fpsDisplay (debug/FpsDisplay.)]
  ;(.render fpsDisplay (. js/document (getElementById "fpsdisplay"))))

(def app-state
  (atom {:yourname (str "Guest" (rand-int 9999))
         :yourlib []
         :rooms muuuuu.mock.make-roomslist
         :player {:tracknumber "Nothing playing at the moment"
                  :tracktitle ""
                  :artist ""
                  :album ""}
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
        (om/build chatwindow/init (:rooms state))
        (om/build catalogue/init (:catalogue state) {:opts {:state state}})
        (om/build roomlist/init (:rooms state))
        (om/build chatinput/init state)
        (om/build modalwindow/modal (:modal state))
        (om/build musicplayer/init (:player state))))))

(om/root
  container
  app-state
  {:target (. js/document (getElementById "app"))})
