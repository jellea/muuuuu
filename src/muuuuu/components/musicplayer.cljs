(ns muuuuu.components.musicplayer
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om.dom :as dom :include-macros true]))



(defn toggle-play-pause [state]
  (om/transact! state
    #(assoc % :is-playing (not (:is-playing %)))))

(defn next-track [state]
  (prn @state)
)

(defn empty-playlist [state]
  (prn @state)
)

(defn love-track [state]
  (prn @state)
)

(defn init [{:keys [tracknumber tracktitle artist
                    data-url playlist is-playing] :as state} owner]
  (reify
    om/IDidUpdate
      (did-update [_ _ _]
        (if is-playing
          (.play (. js/document (getElementById "audioplayer")) nil)
          (.pause (. js/document (getElementById "audioplayer")) nil))
      )
    om/IDidMount
      (did-mount [_]
        (.addEventListener (. js/document (getElementById "audioplayer"))
          "ended" #(next-track state))
      )
    om/IRender
      (render [_]
        (html [:div.musicplayer
              [:div.text.hide (str tracknumber ". " artist " - " tracktitle)]
              [:div.heart.hide "<3"]
              [:audio#audioplayer {:src data-url}]
              [:div.playbtn {:onClick #(toggle-play-pause state)}
                (if is-playing [:img {:src "resources/img/play2.svg"}] [:img {:src "resources/img/pause.svg"}])]]))))
