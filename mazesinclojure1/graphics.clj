(ns mazesinclojure1.graphics)

(import java.awt.Color)
(import java.awt.image.BufferedImage)
(import javax.imageio.ImageIO)

(def img-width 600)
(def img-height 600)

(defn print-to-png
    [maze]
    (let [
          rows (count maze)
          columns (count (maze 0))

          maze-width (int (* img-width 0.95))
          maze-height (int (* img-height 0.95))
          cell-width (int (/ maze-width columns))
          cell-height (int (/ maze-height rows))
          startX (int 10)
          startY (int 10)

          bi (BufferedImage. img-width img-height BufferedImage/TYPE_INT_ARGB)
          graf (.createGraphics bi)]

        (.setColor graf Color/WHITE)
        (.fillRect graf 0 0 img-width img-height)
        (.setColor graf Color/BLACK)
        (.setStroke graf (java.awt.BasicStroke. 3))

        ; draw the top and left edges
        (.drawLine graf startX startY (+ startX (* columns cell-width)) startY)
        (.drawLine graf startX startY startX (+ startY (* rows cell-height)))

        (loop [r (- (count maze) 1) offset cell-height]
            (if (< r 0)
                r
                (do
                    (loop [x startX y startY col 0]
                        (if (< x maze-width)
                            (do   ;1 = gap 0 = line  (str (:distance (nth (nth (binary-tree/carve-passages 4 4) 0) 0)))
                                (if (not= (:distance (nth (nth maze r) col)) -1) (.drawString graf (str (:distance (nth (nth maze r) col))) (int (+ x (/ cell-width 2))) (int (+ (- y (/ cell-height 2)) offset))))
                                (if (not= (:path (nth (nth maze r) col)) -1) (do (.setColor graf Color/RED)(.drawOval graf  x (+ (- y cell-height) offset) cell-width cell-height)))
                                (.setColor graf Color/BLACK)
                                (if (= 0 (:south (nth (nth maze r) col)))
                                    (.drawLine graf x (+ y offset) (+ x cell-width) (+ y offset)))
                                (if (= 0 (:east (nth (nth maze r) col)))
                                    (.drawLine graf (+ x cell-width) (+ y (- offset cell-height)) (+ x cell-width) (+ y offset)))
                                (recur (+ x cell-width) y (inc col)))))
                (recur (dec r) (+ offset cell-height))))
            )

        (ImageIO/write bi "png" (clojure.java.io/file "maze.png"))))

