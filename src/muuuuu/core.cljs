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
            [muuuuu.controllers.files :as files]
            [goog.debug :as debug]
            [goog.debug.FpsDisplay]
            [muuuuu.utils :refer [guid get-next-color]]
            ))

(enable-console-print!)


;(let [fpsDisplay (debug/FpsDisplay.)]
  ;(.render fpsDisplay (. js/document (getElementById "fpsdisplay"))))

(def app-state
  (atom {:yourname (str "Guest" (rand-int 9999))
         :yourlib {:mostlistened [] ;muuuuu.mock.albumcovers
                   :files []}
         :rooms muuuuu.mock.make-roomslist
         :player {:tracknumber "Nothing playing at the moment"
                  :tracktitle ""
                  :artist ""
                  :album ""
                  :is_playing false
                  :data_url ""
                  :playlist []}
         :modal {:hidden true}
         :catalogue {:whos "Your Library"}}))

;(add-watch app-state :everything #(prn %3))

(defn setup-controllers [state]
  (muuuuu.mock.mock state)
  (muuuuu.events.user_events/file-drop)
  (files/add-dropped-files-to-state state)
)

(defn container [state owner]
  (reify
    om/IWillMount
    (will-mount [_])
    om/IDidMount
    (did-mount [_]
      ; start mocking data
      (setup-controllers state)
      ;(muuuuu.events.user_events.init-history app-state)
    )
    om/IRender
    (render [_]
      (dom/div #js {:className "container"}
        (om/build chatwindow/init (:rooms state) {:opts {:state state}})
        (om/build catalogue/init {:yourlib (:yourlib state)
                                  :catalogue (:catalogue state)} {:opts {:state state}})
        (om/build roomlist/init (:rooms state))
        (om/build chatinput/init state)
        (om/build modalwindow/modal (:modal state))
        (om/build musicplayer/init (:player state))))))

(om/root
  container
  app-state
  {:target (. js/document (getElementById "app"))})
