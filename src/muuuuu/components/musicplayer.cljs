(ns muuuuu.components.musicplayer
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om.dom :as dom :include-macros true]))

(defn init [app owner]
  om/component
    (html [:div.musicplayer ">"]))
