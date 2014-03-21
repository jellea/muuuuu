(ns muuuuu.controllers.files
  (:require [goog.object]
            [om.core :as om :include-macros true]
            [cljs.core.async :as async :refer [>! <! alts! chan sliding-buffer put! close!]]
            [goog.async.Throttle]
            [goog.gears.WorkerPool :as WorkerPool]
            [muuuuu.controllers.notifications :as notify]
            [goog.events :as events]))

(def files (atom []))

(defn js-values [obj]
  (let [keys (array)]
    (goog.object/forEach obj (fn [val key obj] (.push keys val)))
    keys))

(defn add-dropped-files-to-state [state]
  (add-watch files :conj
    (fn [_ _ _ files] 
      (om/transact! state [:yourlib] #(assoc % :files files)))))

(defn process-file [entry]
  (let [out (chan)]
      (put! chan "file")
    out))

(defn walk-through-tree [filelist]
  (doseq [entry filelist]
    (do
      (cond (.-isFile entry) (swap! files conj {:file entry :path (.-fullPath entry)})
            (.-isDirectory entry) (do (let [reader (.createReader entry)]
                                      (.readEntries reader walk-through-tree)))))))

(defn drop-new-files [event]
  (let [filelist (-> event .getBrowserEvent .-dataTransfer .-items)]
      (dorun
        (let [flist (for [value (js-values filelist) :when (.-type value)] (.webkitGetAsEntry value))]
          (walk-through-tree flist)
          ))))
