(ns mazesinclojure1.recursive_backtracker)

(defn make-a-row [columns]
  (loop [count 0 row []]
    (if (= columns count)
      row
      (recur (inc count) (conj row {:north 0 :east 0 :south 0 :west 0 :distance -1 :path -1})))))


(defn make-a-grid [rows columns]
  (loop [count 0 grid []]
    (if (= rows count)
      grid
      (recur (inc count) (conj grid (make-a-row columns))))))


(defn valid? [cell maze]                                    ;checks to see if neighbours have been added
  (every? int? (vals (take 4(nth (nth maze (:row cell)) (:col cell))))))

(defn unvisited-valid? [cell grid]                        ;check if the passed cell is unvisited and valid
  (if (and (< (:row cell) (count grid)) (< (:col cell) (count (first grid)))(>= (:row cell) 0)(>= (:col cell) 0))(valid? cell grid) false
                                                                                                                 ))

(defn make-cell [cell n-row n-col direction]
  (zipmap [:row :col :direction] [(+ (:row cell) n-row) (+ (:col cell) n-col) direction])
  )

(defn next-cell [cell direction]
  (cond
    (= direction :north) (make-cell cell 1 0 :south)       ;move north add inverse direction to new cell
    (= direction :east)  (make-cell cell 0 1 :west)       ;move east ""
    (= direction :south) (make-cell cell -1 0 :north)      ;move south ""
    (= direction :west)  (make-cell cell 0 -1 :east)      ;move west ""
    ))

(defn break-the-wall [c-cell n-cell direction grid]         ;cell is valid so add as neighbour to current cell
  (assoc-in (assoc-in grid [(:row c-cell) (:col c-cell) direction] (into {} (take 2 n-cell))) [(:row n-cell) (:col n-cell) (:direction n-cell)] (into {} (take 2 c-cell))))

(defn carve-passages [c-cell grid]
  (reduce (fn [grid direction]                          ;reduce grid into next direction
            (let [n-cell (next-cell c-cell direction)]  ;get next cell
              (if (unvisited-valid? n-cell grid)        ;check if next cell valid
                (carve-passages n-cell            ;continue path with next cell
                                (break-the-wall c-cell n-cell direction grid)) ;add next cell as neighbour
                grid))) ;next not valid
          grid, (clojure.core/shuffle [:north :south :east :west])))

(defn caller [rows cols]
  (carve-passages {:row 0 :col 0} (make-a-grid rows cols)))








