(ns primetimes.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))



(defn calc-limit [n]
  (let [log (Math/log n)
        loglog (Math/log log)
        logsum (+ log loglog)]
    (-> n (* logsum) int (+ 3))))

(defn primes [n]
  (let [root (-> n (Math/sqrt) inc int)
        sieve (boolean-array n true)]
    (pln :seiving)
    (loop [i 2]
      (when (< i (Math/sqrt n))
        (when (aget sieve i)
          (loop [j (* i 2)]
            (when (< j n)
              (aset sieve j false)
              (recur (+ j i)))))
        (recur (inc i))))
    (filter #(aget sieve %) (range 2 n))))

(defn nth-prime [n]
  (cond
    (= n 1) 2
    (= n 2) 3
    :else (last (take n (primes (calc-limit n))))))

(defn nth-prime [n]
  (cond
    (= n 1) 2
    (= n 2) 3
    :else (last (take n (primes (calc-limit n))))))

(defn first-n-primes [n]
  (take n (primes (calc-limit n))))
