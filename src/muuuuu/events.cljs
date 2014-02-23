(ns muuuuu.events)

(defn init-panelsnap "initiate panelsnap on .chat"[]
  (.panelSnap (js/$ ".chat")
              #js {:$menu (js/$ ".joinchatmenu")
                   :slideSpeed 200
                   :menuSelector "li"}))

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
