(ns mazesinclojure1.core
  (:use [seesaw.core]
        [seesaw.font]
        [seesaw.border]
        [seesaw core graphics])
  (:require [mazesinclojure1.binary_tree_immutable :as binary_tree_immutable]
            [mazesinclojure1.binary_tree_mutable :as binary_tree_mutable]
            [mazesinclojure1.graphics :as graffix]
            [mazesinclojure1.pathfinding :as pathfinder]
            [mazesinclojure1.recursive_backtracker :as backtracker]
            )
  )

(import javax.imageio.ImageIO)

(defn draw-image                                            ;paint maze to panel
  [c g]
  (draw g (image-shape 0 0 (ImageIO/read (clojure.java.io/file "maze.png"))) (style))
  )


(defn row-col-pnl []
  (let [
        x-label (label :text "Columns"
                       :font (font :name :serif :size 10))
        y-label (label :text "Rows"
                       :font (font :name :serif :size 10))
        x-in (text :text 4 :columns 3 :id :x-in)
        y-in (text :text 4 :columns 3 :id :y-in)
        y-start-label (label :text "Distances start - Column"
                       :font (font :name :serif :size 10))
        x-start-label (label :text "Distances start - Row"
                       :font (font :name :serif :size 10))
        x-start-in (text :text 0 :columns 3 :id :x-start-dist)
        y-start-in (text :text 0 :columns 3 :id :y-start-dist)
        x-end-label (label :text "Solver end - Column"
                             :font (font :name :serif :size 10))
        y-end-label (label :text "Solver end - Row"
                             :font (font :name :serif :size 10))
        x-end-in (text :text 3 :columns 3 :id :x-end-in)
        y-end-in (text :text 3 :columns 3 :id :y-end-in)
        panel (grid-panel :rows 6 :columns 2 :hgap 2
                          :items [x-label x-in y-label y-in x-start-label x-start-in y-start-label y-start-in x-end-label x-end-in y-end-label y-end-in])
        ]
    panel))

(defn btn-panel [output]
  (let [
        generate-btn-binary-tree (button :text "Generate binary tree maze"
                             :font (font :name :sans-serif :size 14))
        generate-btn-backtracker (button :text "Generate backtracker maze"
                                         :font (font :name :sans-serif :size 14))
        maze-distances-btn (button :text "Add distances"
                                 :font (font :name :sans-serif :size 14))
        solver-btn (button :text "Solve the maze"
                           :font (font :name :sans-serif :size 14))
        row-col-pnl (row-col-pnl)
        panel (vertical-panel
                :border 5
                :items [
                        generate-btn-binary-tree
                        generate-btn-backtracker
                        maze-distances-btn
                        row-col-pnl
                        solver-btn])]
    (listen generate-btn-binary-tree :action
            (fn [e]
              (let [columns (Integer/parseInt (value (select row-col-pnl [:#x-in])))
                    rows (Integer/parseInt (value (select row-col-pnl [:#y-in])))
                    maze (binary_tree_immutable/carve-passages rows columns)]
                (spit "maze_structure.clj" (pr-str maze))   ;save maze data structure to file
                (graffix/print-to-png (read-string (slurp "maze_structure.clj")))
                (config! (select output [:#canvas]) :paint draw-image)
                )))
    (listen generate-btn-backtracker :action
            (fn [e]
              (let [columns (Integer/parseInt (value (select row-col-pnl [:#x-in])))
                    rows (Integer/parseInt (value (select row-col-pnl [:#y-in])))
                    maze (backtracker/caller rows columns)]
                (spit "maze_structure.clj" (pr-str maze))   ;save maze data structure to file
                (graffix/print-to-png (read-string (slurp "maze_structure.clj")))
                (config! (select output [:#canvas]) :paint draw-image)
                )))
    (listen maze-distances-btn :action                        ;(read-string (slurp "maze_structure.clj")) load maze
            (fn [e]
              (let [start-row (Integer/parseInt (value (select row-col-pnl [:#x-start-dist])))
                    start-col (Integer/parseInt (value (select row-col-pnl [:#y-start-dist])))
                    maze (pathfinder/distances (read-string (slurp "maze_structure.clj")) start-row start-col 0)]
                ;(spit "maze_structure.clj" (pr-str maze))
                (graffix/print-to-png maze)
                (config! (select output [:#canvas]) :paint draw-image)
                )
              ))
    (listen solver-btn :action
            (fn [e]
              (let [end-col (Integer/parseInt (value (select row-col-pnl [:#x-end-in])))
                    end-row (Integer/parseInt (value (select row-col-pnl [:#y-end-in])))
                    start-col (Integer/parseInt (value (select row-col-pnl [:#x-start-dist])))
                    start-row (Integer/parseInt (value (select row-col-pnl [:#y-start-dist])))
                    maze (pathfinder/get-path (pathfinder/distances (read-string (slurp "maze_structure.clj")) start-row start-col 0) end-row end-col)]
                ;(spit "maze_structure.clj" (pr-str maze))
                    (graffix/print-to-png maze)
                    (config! (select output [:#canvas]) :paint draw-image)
                )
              ))
    panel))


(defn image-panel []
  (let [panel (canvas :id :canvas :background "#BBBBDD" :paint nil)]
    panel))


(defn content []
  (let [
        image-panel (image-panel)
        btn-panel (btn-panel image-panel)
        panel (border-panel :hgap 5 :vgap 5 :border 5
                            :west btn-panel
                            :center image-panel)]
    panel))

(defn -main [& args]
  (invoke-later
    (-> (frame :title "Demonstrating SeeSaw",
               ;:width 950                                   ;disabled by pack! for some reason
               ;:height 650                                  ;also here
               :minimum-size [950 :by 650]                  ;not here
               :content (content)
               )                                            ;:on-close :exit
        pack!
        show!)))

(-main)

