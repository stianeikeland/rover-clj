(ns rover.api-test
  (:use midje.sweet)
  (:require [rover.api :refer :all]))


;; The rover receives a character array of commands.

;; Implement commands that move the rover forward/backward (f,b).

;; Implement commands that turn the rover left/right (l,r).

;; Example: The rover is on a 100x100 grid at location (0, 0) and facing NORTH.
;; The rover is given the commands "ffrff" and should end up at (2, 2)

;; Tips: use multiple classes and TDD

(def world [[:_ :_ :_]
            [:_ :_ :_]
            [:_ :_ :_]])

(def game {:world world
           :direction :north
           :position [1 1]})

;; Develop an api that moves a rover around on a grid.
;; You are given the initial starting point (x,y) of a rover and the direction
;; (N,S,E,W) it is facing.

;;; Test helpers

(defn game-direction [dir]
  (assoc game :direction dir))

;;; Basic movement

(fact "forward should move south when facing south"
  (-> (game-direction :south)
      forward
      :position) => [2 1])

(fact "forward should move north when facing north"
  (-> (game-direction :north)
      forward
      :position) => [0 1])

(fact "forward should move east when facing east"
  (-> (game-direction :east)
      forward
      :position) => [1 2])

(fact "forward should move west when facing west"
  (-> (game-direction :west)
      forward
      :position) => [1 0])

(fact "backward should move north when facing south"
  (-> (game-direction :south)
      backward
      :position) => [0 1])


;;; Turning

(fact "turn left when north should return west"
  (-> (game-direction :north)
      left
      :direction) => :west)

(fact "turn right when north should return east"
  (-> (game-direction :north)
      right
      :direction) => :east)


;;; Planet wrapping

;; Implement wrapping from one edge of the grid to another. (planets are spheres
;; after all)

(fact "rover should end up on bottom when moving over the top"
  (-> game
      forward
      forward
      :position) => [2 1])

(fact "rover should end up to the east when moving over the west edge"
  (-> game
      left
      forward
      forward
      :position) => [1 2])


;;; Obstacle detection

;; Implement obstacle detection before each move to a new square. If a given
;; sequence of commands encounters an obstacle, the rover moves up to the last
;; possible point and reports the obstacle.

(def obstacle-world [[:_ :X :_]
                     [:_ :_ :_]
                     [:X :X :X]])

(def obstacle-game {:world obstacle-world
                    :direction :north
                    :position [1 1]})

(fact "rover should not move over obstacle"
  (-> obstacle-game
      forward) => (throws Exception "I can't do that, Dave!"))


;;; Execute command sequences

;; The rover receives a character array of commands.

(fact "rover should accept string sequence"
  (->> "lfrrff"
       (execute obstacle-game)
       :position) => [1 2])

(fact "rover throws if string of operations hits an obstacle"
  (->> "lfrrflffffff"
       (execute obstacle-game)) => (throws Exception))

(fact "rover should return last possible map on exception"
  (try (->> "lfrrflffffff"
            (execute obstacle-game)
            (ex-data))
       (catch clojure.lang.ExceptionInfo e
         (-> (ex-data e)
             :position))) => [1 1])


;;; World Generation

(fact "generates random world"
  (->> (new-game 100 100)
       :world
       flatten
       frequencies
       :X) => (roughly 5000 4999))
