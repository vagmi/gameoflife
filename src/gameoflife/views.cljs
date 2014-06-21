(ns gameoflife.views
  (:require [om.core :as om :include-macros true]
            [gameoflife.core :as gol]
            [cljs.core.async :as async
             :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
            [om.dom :as dom :include-macros true])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(enable-console-print!)
(def blinker 
  (-> {}
      (assoc [4 5] true)
      (assoc [5 5] true)
      (assoc [6 5] true)))
(def glider
  (-> {}
      (assoc [5 4] true)
      (assoc [5 5] true)
      (assoc [5 6] true)
      (assoc [4 6] true)
      (assoc [3 5] true)
      ))
(def board-size 71)
      
(def cells (for [x (range board-size) 
                 y (range board-size)] [x y]))
(def app-state (atom {:board (gol/init-board glider board-size board-size)
                      :history nil}))

(defn cell-view [cell ]
  (reify
    om/IRender
    (render [this]
      (dom/span #js {:className (str "cell " (if cell "alive"))}
                ""))))

(defn row-view [row ]
  (reify
    om/IRender
    (render [this]
      (apply dom/div #js {:className "row"}
             (om/build-all cell-view row)))))

(defn board-view [app owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/div nil
        (om/build-all row-view (partition board-size (map #(get (:game (:board app)) %) cells)))))))

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "app"))})

(go
  (loop [ctr 1]
    (<! (timeout 50))
    (let [b (:board @app-state)
          evolved (gol/evolve b)]
      (swap! app-state #(assoc % :board evolved)))
    (recur (inc ctr))))
