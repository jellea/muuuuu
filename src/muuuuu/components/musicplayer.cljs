(ns muuuuu.components.musicplayer
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om.dom :as dom :include-macros true]))

(defn init [{:keys [tracknumber tracktitle artist dataurl] :as app} owner]
  (reify
    om/IRender
      (render [_]
        (html [:div.musicplayer
              [:div.text.hide (str tracknumber ". " artist " - " tracktitle)]
              [:div.heart.hide "<3"]
              [:audio#audioplayer {:src dataurl}]
              [:div.playbtn ">"]]))))
