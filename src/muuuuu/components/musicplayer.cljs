(ns muuuuu.components.musicplayer
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn init [app owner]
  om/component
    (dom/div #js {:className "musicplayer"} ">"))

;<div class="musicplayer" ng-include src="'views/musicplayer.html'">
;</div>
