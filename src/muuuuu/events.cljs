(ns muuuuu.events)

(defn up-and-down-keys "Bind panelsnap to up and down arrow keys." []
  (.add js/shortcut
    "Up"
    #(.panelSnap (js/$ ".chat") "snapTo" "prev")
    #js {:type "keydown" :propagate false :target js/document}
  )

  (.add js/shortcut
    "Down"
    #(.panelSnap (js/$ ".chat") "snapTo" "next")
    #js {:type "keydown" :propagate false :target js/document}
))

;(.onclose js/window
  ;(fn [e] (if (.confirm js/window "Wanna leave?" false)
    ;(.preventdefault e))))
