(ns mazesinclojure1.binary_tree_immutable)

(defn make-a-row [columns]
  (loop [count 0 row []]
    (if (= columns count)
      row
      (recur (inc count) (conj row {:north 0 :east 0 :south 0 :west 0 :distance -1 :path -1}))))) ;would be nice if this was a type called cell or something


(defn make-a-grid [rows columns]
  (loop [count 0 grid []]
    (if (= rows count)
      grid
      (recur (inc count) (conj grid (make-a-row columns))))))

(defn break-the-wall
  [grid row size cells col]
  (cond
    ; above top row return maze
    (= row size) grid
    ; top row && last cell do nothing
    (and (= row (- size 1))
         (= col (- cells 1))) grid
    ; top row carve east
    (=  row (- size 1)) (assoc-in (assoc-in grid [row col :east] (zipmap [:row :col][row (+ col 1)])) [row (+ col 1) :west] (zipmap [:row :col][row (- (+ col 1) 1)]))
    ; not top row && last cell carve north
    (= col (- cells 1)) (assoc-in (assoc-in grid [row col :north] (zipmap [:row :col][(+ row 1) col])) [(+ row 1) col :south] (zipmap [:row :col][(- (+ row 1) 1) col]))
    :else
    (if (= 0 (rand-int 2))
      (assoc-in (assoc-in grid [row col :east] (zipmap [:row :col][row (+ col 1)])) [row (+ col 1) :west] (zipmap [:row :col][row (- (+ col 1) 1)]))
      (assoc-in (assoc-in grid [row col :north] (zipmap [:row :col][(+ row 1) col])) [(+ row 1) col :south] (zipmap [:row :col][(- (+ row 1) 1) col])))))




(defn walk-a-row
  [g current-row]
  (let [num-rows (count g) num-cols (count (g 0))]
    (loop [grid g col 0]
      (if (= num-cols col)
        grid
        (recur (break-the-wall grid current-row num-rows num-cols col) (inc col))))))


(defn carve-passages
  [rows columns]
  (loop [grid (make-a-grid rows columns) current-row 0]
    (if (= current-row rows)
      grid
      (recur (walk-a-row grid current-row) (inc current-row)))))
