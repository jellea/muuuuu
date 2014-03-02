(ns muuuuu.events.user_events
  (:require [muuuuu.dispatcher :as dispatch]
            [goog.events :as events]
            [goog.events.FileDropHandler :as FileDropHandler]))

; when dropping files on window
(let [handler (events/FileDropHandler. js/document true)]
  (events/listen handler (-> events/FileDropHandler .-EventType .-DROP)
      #(dispatch/fire :file-dropped [%])))
