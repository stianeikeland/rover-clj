(ns rover.console
  (:require [rover.api :refer :all]
            [clansi.core :refer [style]]))

(def terrain {:X (style "#" :yellow)
              :_ " "
              :north (style "Λ" :red :blink-slow :bright)
              :south (style "V" :red :blink-slow :bright)
              :west (style "<" :red :blink-slow :bright)
              :east (style ">" :red :blink-slow :bright)})

(defn print-map [game]
  (let [world (assoc-in (:world game) (:position game) (:direction game))]
    (doseq [row (mapv (partial map terrain) world)]
      (println (apply str row)))))

(defn play [game]
  (try (loop [game game]
         (print-map game)
         (println "Where do you want to move?")
         (let [input (read-line)]
           (when-not (= input "q")
             (recur (execute game input)))))
       (catch Exception e
         (print-map (ex-data e))
         (println (style (.getMessage e) :red)))))

(defn play-game []
  (play (new-game 24 80)))

(defn -main []
  (play-game))
