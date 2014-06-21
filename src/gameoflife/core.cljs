(ns gameoflife.core
  (:require-macros [cljs.core.match.macros :refer [match]])
  (:require [cljs.core.match]))

(enable-console-print!)

(defprotocol IGame
  (out-of-bounds? [_ cell])
  (neighbors [_ xpos ypos])
  (is-alive? [_ cell])
  (should-recalculate-for [_])
  (next-state [_ cell])
  (evolve [_]))


(defrecord BoundedBoard [game width height]
  IGame
  (out-of-bounds? [_ cell]
    (or (< (first cell) 0)
        (< (second cell) 0)
        (> (first cell) width)
        (> (second cell) height)))
  (neighbors [self xpos ypos]
    (filter #(not (or (= % [xpos ypos]) 
                      (out-of-bounds? self %))) 
            (for [dx [-1 0 1] dy [-1 0 1]] 
              [(+ xpos dx) (+ ypos dy)])))
  (is-alive? [self cell]
    (get (:game self) cell))
  (should-recalculate-for [self]
    (let [live-cells (keys game)]
      (into #{}
            (apply concat live-cells
                   (map #(apply neighbors self %) live-cells)))))
  (next-state [self cell]
    (let [alive-cells (filter #(is-alive? self %) (apply neighbors self cell))
          alive-count (count alive-cells)
          am-i-alive? (= true (is-alive? self cell))
          is-underpopulated? (< alive-count 2)
          is-overcrowded? (> alive-count 3)]
      (match [am-i-alive? alive-count is-underpopulated? is-overcrowded?]
             [true  _  true _   ]    nil
             [true  _  _    true]    nil
             [true  2  _    _   ]    true
             [true  3  _    _   ]    true
             [false 3  _    _   ]    true
             :else    nil)))
  (evolve [self]
    (let [
        cells (should-recalculate-for self)
        evolved (reduce #(assoc %1 %2 (next-state self %2)) 
                                game cells)
        filterd (into {} (for [ [k v] evolved :when (= v true)] [k v]))]
      (BoundedBoard. filterd width height))))



(defn init-board [pattern width height]
  (BoundedBoard. pattern width height))

(comment
  (defn setup-blinker []
    (let [pattern (-> {}
                      (assoc [4 5] true)
                      (assoc [5 5] true)
                      (assoc [6 5] true))]
      (reset! board (BoundedBoard. pattern 20 20))))
  (defn alive-cells [board]
    (into #{}
          (filter #(is-alive? board %) (keys (:game board)))))
  (setup-blinker)
  (alive-cells @board)
  (= (alive-cells @board) (alive-cells (evolve (evolve @board)))))
