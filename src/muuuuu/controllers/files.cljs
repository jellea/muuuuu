(ns muuuuu.controllers.files
  (:require
            [goog.object]
            [goog.async.Throttle]
            [goog.gears.WorkerPool :as WorkerPool]
            [muuuuu.controllers.notifications :as notify]
            [goog.events :as events]))

(defn js-values [obj]
  (let [keys (array)]
    (goog.object/forEach obj (fn [val key obj] (.push keys val)))
    keys))

(def yfiles (atom []))

(add-watch yfiles :conj
  (fn [_ _ _ msg]  (.log js/console msg))
  ; make worker read file and get mp3 metadata
  ;#(reset! notify/state {:title "muuuuu" :message "x files are added"})
)

(defn js-object? [obj]
  (prn (type obj))
  (identical? (type obj) js/Object))

; check if files are DataTransferItem and have webkitGetAsEntry

(defn make-tree [filelist]
  (prn "maketree!")
  (doseq [entry filelist]
    (do
        (cond (.-isFile entry) (swap! yfiles conj entry)
              (.-isDirectory entry) (do (let [reader (.createReader entry)]
                                        (.readEntries reader make-tree)))))))

(defn read-filelist [event]
  (let [filelist (-> event .getBrowserEvent .-dataTransfer .-items)]
    (dorun (make-tree (for [value (js-values filelist) :when (.-type value)] (.webkitGetAsEntry value))))
  ))

(defn drop-new-files [e] (read-filelist e))
