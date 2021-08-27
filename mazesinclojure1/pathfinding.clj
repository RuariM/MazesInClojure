(ns mazesinclojure1.pathfinding
  (:require [mazesinclojure1.binary_tree_immutable :as binary_tree_immutable]))


(defn find-cells [maze distance]
  ;get cells with current distance
  (remove nil? (for [row (range (count maze)) col (range (count(first maze)))]
                 (if (= (:distance(nth (nth maze row) col))distance) (nth (nth maze row) col))))
  )

(defn next-cell [maze distance neighbour]
  (cond
    ;no neighbour
    (= neighbour 0) maze
    ;if unvisited neighbour
    (= (:distance (nth(nth maze (:row neighbour))(:col neighbour)))-1)
        (assoc-in maze [(:row neighbour)(:col neighbour) :distance] distance)
    :else maze
    )
  )

(defn visit-cells [maze cells distance]
  (loop [maze maze cell cells distance distance]
    (cond                    ;check if unvisited
      (empty? cell) maze
      ;check each direction
      :else
      (recur (loop [maze maze neighbours (take 4 (first cell))] ;could use reduce
               (cond
                 (empty? neighbours) maze
                 :else                    ;set value of neighbour cells to current distance + 1        ;pass neighbour index
                 (recur (next-cell maze (+ distance 1) (second (first neighbours)))(rest neighbours)))) (rest cell) distance)
      )
    )
  )

(defn distances [start-maze start-r start-c distance]
  ;keep finding cells until
  (loop [maze (assoc-in start-maze [start-r start-c :distance] distance) distance distance]
    ;visit cells at current distance
    (cond
      (not= (:distance (apply min-key :distance (flatten maze)))-1) maze ;exit if all cells visited
      ;visit cells with current distance
      :else (recur (visit-cells maze (find-cells maze distance) distance) (inc distance))
      )
    )
  )


(defn smallest-neighbour [cell maze]
                    (into {} (take 2(apply min-key :distance(remove nil?(for [c (take 4 (nth (nth maze (:row cell)) (:col cell)))] ;get smallest neighbour index
                                                                 (if (not= (second c) 0) (zipmap [:row :col :distance]
                                                                                          [(:row (second c)) ; get cell index
                                                                                           (:col (second c))
                                                                                           (:distance(nth (nth maze (:row (second c))) (:col (second c))))])))))))) ;get distance

(defn get-path [maze start-r start-c]
          (loop [distance (+(:distance (nth (nth maze start-r)start-c))1) maze (assoc-in maze [start-r start-c :path] 0) cell (zipmap [:row :col] [start-r start-c])]
            (cond
              (= distance 0)maze
              :else (recur (dec distance) (assoc-in maze [(:row cell) (:col cell) :path] distance) (smallest-neighbour cell maze)))))
                                                          ;need to use index

