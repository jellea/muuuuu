(ns muuuuu.components.catalogue
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.fx]
            [goog.fx.DragDrop]
            [goog.fs]
            [goog.fs.FileReader]
            [goog.fx.DragDropGroup]
            [muuuuu.events.user_events :refer [rightkey-show-catalogue]]
            [sablono.core :as html :refer-macros [html]]
            ))

(def releases-dnd-group (goog.fx.DragDropGroup.))

(defn add-new-target [elemid elemgroup]
  (let [new-target (goog.fx.DragDrop. elemid)]
    (.init new-target)
    (set! (.-outlineColor (.-style (.getElementById js/document elemid))) "rgba(255,255,255,0.3")
    (.addTarget elemgroup new-target new-target)
    (.init elemgroup)))

(defn init-drag-drop
  "Initiate drag and drop listeners group"
  [state]
    ; TODO BUG: target frames are misaligned after shifting of .chat div.
    (add-new-target "chatinput" releases-dnd-group)
    (let [dropzone (.getElementById js/document "app")]
      (events/listen releases-dnd-group "drop" #(prn))
      (events/listen releases-dnd-group "dragstart" (fn [e]
        (.add (.-classList dropzone) "dropzone")
        (set! (-> e .-dragSourceItem .-element .-style .-opacity) 0.3)))
      (events/listen releases-dnd-group "dragover" (fn [e]
        (set! (-> e .-dropTargetItem .-element .-style .-outlineColor) "rgba(255,255,255,1)")))
      (events/listen releases-dnd-group "dragout" (fn [e]
        (set! (-> e .-dropTargetItem .-element .-style .-outlineColor) "rgba(255,255,255,0.3)")))
      (events/listen releases-dnd-group "dragend" (fn [e]
        (.remove (.-classList dropzone) "dropzone")
        (set! (-> e .-dragSourceItem .-element .-style .-opacity) 1)))))

(defn test-modal []
  (om/component
    (html [:div [:h3 "add music"]
                [:p "Soon you will be able to connect your soundcloud, bandcamp, whatsover"]])))

(defn show-add-modal [state]
  (om/transact! state
    #(assoc % :modal {:hidden false :component test-modal})
  ))

(defn back-to-lib [state]
  (om/transact! state
    #(assoc % :catalogue {:whos "Your Library"})))

(defn press-play [file state]
  (.file (:file @file) (fn [blob]
    (let [player (. js/document (getElementById "audioplayer"))
          reader (js/FileReader.)]
      (set! (.-onload reader) (fn [e]
        (om/transact! state [:player]
          (fn [player]
            (merge player
              {:data-url (.-result (.-target e))
               :is-playing true
               :tracknumber (:path @file)})))))
      (.readAsDataURL reader blob)))))

(defn release
  "Release component"
  [{:keys [img] :as app} owner {:keys [releasesgroup] :as opts}]
  (reify
    om/IDidMount
    (did-mount [_]
      ; add to dragdropgroup
      (.addItem releases-dnd-group (om/get-node owner "release") img)
    )
    om/IRender
    (render [_]
    (html [:div.release {:ref "release"}
            [:img {:src img}]]))))

(defn file-comp
  "File component"
  [{:keys [path] :as file} owner {:keys [state] :as opts}]
  (om/component
    (html [:li 
            [:a {:onClick #(press-play file state)} "play "]
            path])))

(defn init
  "Library (right) sidebar component"
  [{:keys [yourlib catalogue]} owner {:keys [state] :as opts}]
    (reify
      om/IWillMount
      (will-mount [_]
        ; Get 'yourlib' from appstate and put in catalogue
        (if (= (:whos catalogue) "Your Library")
          (om/transact! catalogue #(merge % (:yourlib state)))))
      om/IDidMount
      (did-mount [_]
        (init-drag-drop state))
      om/IRender
      (render [_]
          (html 
            [:aside.catalogue
              [:h2
                (:whos catalogue)
                ;(if (= (:whos catalogue) "Your Library")
                  ;[:a.add {:onClick #(show-add-modal state)} "add"]
                  ;[:a.add {:onClick #(back-to-lib state)} "< back"])
              ]
              [:div.releases
                  (if (> (count (:mostlistened yourlib)) 0) [:h3 "most listened"])
                    (om/build-all release (take 8 (:mostlistened yourlib))
                        {:key :id :opts {:releasesgroup releasesgroup}})
                  (if (> (count (:files yourlib)) 0)
                    [:h3 "folders"])
                    [:ul.files (om/build-all file-comp (:files yourlib) {:opts {:state state}})]
              ]]))))
