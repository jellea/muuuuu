(ns muuuuu.controllers.files
  (:require
            [goog.object]
            [goog.events :as events]))

(defn js-values [obj]
  (let [keys (array)]
    (goog.object/forEach obj (fn [val key obj] (.push keys val)))
    keys))

(defn read-filelist [event]
  (let [filelist (-> event .getBrowserEvent .-dataTransfer .-items)]
    (prn (type (aget filelist "0")))
    (prn (vec (for [value (js-values filelist)
        ;:when (contains? (list* (js-values value)) "type")
        ] value)))
  ))

(defn drop-new-files [e] (read-filelist e))
