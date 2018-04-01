(ns primetimes.prime-numbers)

;; --- work derived from Paul "The Rustler" Cowan's Sieve of Erasthones ---

(defn nth-prime-upper-bound
  "We figure out how big can be the nth prime. Where p(n) is the nth prime:

      n*(log n + log (log n) - 1) < p(n) < n*(log n + log (log n)), for n >= 6"

  [n]

  (cond
    ;; above rule works only for n >= 6
    (< n 6) 13
    ;; now the formula
    :default (let [logn (Math/log n)]
               (Math/ceil
                 (* n (+ logn (Math/log logn)))))))

(defn sieve
  "Build a boolean array big enough to hold the
  nth prime where only values at prime indices are true."

  [prime-upper-bound]

  (let [max-factor (Math/sqrt prime-upper-bound)
        sieve (boolean-array prime-upper-bound true)]
    (loop [p 2]
      (when (<= p max-factor)
        (when (aget sieve p)
          ;; we have a prime; propagate out via fast addition.
          ;; we can start at the square because values between
          ;; (+ x x), the apparent next, and (* x x) will have been handled by
          ;; propagation of earlier primes.
          ;;
          ;; eg, when we find 5 is prime, 10, 15, and 20 will have been cleared
          ;; by 2, 3, and 4.
          ;;
          (loop [p-product (* p p)]
            (when (< p-product prime-upper-bound)                                   ;; do not pass end of array
              (aset sieve p-product false)
              (recur (+ p-product p)))))                            ;; look at next multiple of x
        (recur (inc p))))
    sieve))

(defn primes [n]
  (let [sieve (sieve n)]
    (filter #(aget sieve %) (range 2 n))))

(defn first-n-primes [n]
  (take n (primes (nth-prime-upper-bound n))))
