(ns muuuuu.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [muuuuu.components.chatwindow]
            [muuuuu.components.sidebarleft]
            [muuuuu.components.sidebarright]
            [muuuuu.components.chatinput]
            [muuuuu.utils :refer [guid get-next-color]]
            ))

(enable-console-print!)

(.add js/shortcut
  "Up"
  #(.panelSnap (js/$ ".chat") "snapTo" "next")
  #js {:type "keydown" :propagate false :target js/document}
  )

(def app-state
  (atom {:yourname "your name"
         :rooms {:joined []
                 :popular {"Pop" {:users 15 :id (guid)}, "RnB" {:users 26 :id (guid)}}}
         :releases [
                    ;{:img "http://yaj0kz2x.zvq.me/197358b7bdf9735260715f06b79d0fc0c0c29612089e08249fe8d5cfc6e29ed8.300x300.jpg" :id (guid)}
           ;{:img "http://b9hyta0l.zvq.me/3be24a8a8e35024d8d374ad651b56b38e57a3354b403ddb9a996d94d8c9d5a9f.300x300.jpg" :id (guid)}
           ;{:img "http://b9hyw0hl.zvq.me/157e34383a392c299ef305234b04ac3462fc1c75233e32e00357642b2dc90791.300x300.jpg" :id (guid)}
           ;{:img "http://yahws2ks.zvq.me/18886cd26ef34d1a87b8b0fcd27570aee0b8300704989d7a3fa04c50b4358129.300x300.jpg" :id (guid)}
           ;{:img "http://yaj0ggax.zvq.me/06f65fc7f5025e9116883be6c2f7ce61a8d11987dc9e0304eed7468cf52698d9.300x300.jpg" :id (guid)}
           ;{:img "http://yng42nua.zvq.me/cd977c04723e43a3393d0b0dd657431ef169ee5e1ad9f0b4939e151f095abb14.300x300.jpg" :id (guid)}
           ;{:img "http://yaj0gnhx.zvq.me/e6cbac8fd59db704851720e8f0cd01f211fde97b46bbd54d588303687c361701.300x300.jpg" :id (guid)}
                    ]}))

(defn container [app owner]
  (reify
  om/IWillMount
    (will-mount [_])
    om/IDidMount
    (did-mount [_]
      ;(muuuuu.components.sidebarleft.addChannel "Techno" app)
      ;(muuuuu.components.sidebarleft.addChannel "Metal" app)
    )
    om/IRender
    ; if inviewport give class 'selected'
    (render [_]
      (dom/div #js {:className "container"}
           (om/build muuuuu.components.chatwindow.init app)
           (om/build muuuuu.components.sidebarright.init app)
           (om/build muuuuu.components.sidebarleft.init app)
           (om/build muuuuu.components.chatinput.init app)))))

(om/root
  container
  app-state
  {:target (. js/document (getElementById "app"))})
