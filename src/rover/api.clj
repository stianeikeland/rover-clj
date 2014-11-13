(ns rover.api)

(def direction->movement {:north [-1 0]
                          :south [1  0]
                          :west  [0 -1]
                          :east  [0  1]})

(def turn-left {:north :west
                :west  :south
                :south :east
                :east  :north})

(def turn-right {:north :east
                 :east  :south
                 :south :west
                 :west  :north})

(defn- dimensions [game]
  (map count [(-> game :world)
              (-> game :world first)]))

(defn- new-position [game change]
  (let [pos     (:position game)
        new-pos (map + pos change)
        dim     (dimensions game)
        wrapped (map mod new-pos dim)]
    wrapped))

(defn- move [game change]
  (let [new-pos (new-position game change)
        cell    (get-in (:world game) new-pos)]
    (if-not (= cell :_)
      (throw (ex-info "I'm afraid I can't do that, Dave!" game))
      (assoc game :position new-pos))))

(defn- gen-row [length]
  (mapv (fn [_] (rand-nth [:_ :_ :_ :X])) (range length)))


;;; Public API

(defn new-game [height length]
  (let [start-pos [(/ height 2) (/ length 2)]
        raw-world (mapv (fn [_] (gen-row length)) (range height))
        world     (assoc-in raw-world start-pos :_)]
    {:world world
     :direction :north
     :position start-pos}))

(defn forward [game]
  (move game (direction->movement (:direction game))))

(defn backward [game]
  (move game (->> (direction->movement (:direction game))
                  (map * [-1 -1]))))

(defn right [game]
  (update-in game [:direction] turn-right))

(defn left [game]
  (update-in game [:direction] turn-left))

(def op->func {:l left
               :r right
               :f forward
               :b backward})

(defn execute [game commands]
  (let [str->func (comp op->func keyword str)
        funcs     (map str->func commands)]
    (reduce (fn [g f] (f g)) game funcs)))
