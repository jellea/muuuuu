(ns muuuuu.components.musicplayer
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om.dom :as dom :include-macros true]))

(defn init [{:keys [tracknumber tracktitle artist data-url is-playing] :as app} owner]
  (reify
    om/IDidUpdate
      (did-update [_ _ _]
        (if is-playing
          (.play (. js/document (getElementById "audioplayer")) nil))
      )
    om/IRender
      (render [_]
        (html [:div.musicplayer
              [:div.text.hide (str tracknumber ". " artist " - " tracktitle)]
              [:div.heart.hide "<3"]
              [:audio#audioplayer {:src data-url}]
              [:div.playbtn ">"]]))))
