(ns primetimes.prime-numbers-test
  (:require [clojure.test :refer :all]
            [primetimes.prime-numbers :refer :all]
            [com.hypirion.primes :as p]
            [taoensso.tufte :as tufte :refer (defnp p profiled profile)]))

;;
;; we test against https://github.com/hypirion/primes

(defn nth-prime [n]
  (cond
    (= n 1) 2
    (= n 2) 3
    :else (last (take n (primes (nth-prime-upper-bound n))))))

(deftest primegen
  (testing "Get nth prime"
    (let [n 3]
      (is (= 5
            (nth-prime n)
            (p/get (dec n)))))
    (let [n 10]
      (is (= 29
            (nth-prime n)
            (p/get (dec n)))))
    (let [n 30]
      (is (= (nth-prime n)
            (p/get (dec n))))))

  (testing "Sanity check count"
    (dotimes [n 50]
      (let [ps (first-n-primes n)]
        (when (pos? n)
          (is (= 2 (first ps))))
        (is (= n (count ps))))))

  (testing "Sanity check primality"
    (doseq [n (first-n-primes 50)]
      (when (> n 6)
        (let [m6 (mod n 6)]
          ;; necessary but not sufficient test for primality:
          (is (or (= m6 1)(= m6 5)))))))

  (testing "Get first N primes"
    (let [n 10]
      (is (= [2 3 5 7 11 13 17 19 23 29]
            (first-n-primes n)
            (p/take n)))

      (let [n 30]
        (is (= (first-n-primes n)
              (p/take n)))))))

(deftest test-nth-prime-upper-bound
  ;; sanity check new nth-prime-upper-bound against hard-coded values
  ;; where n*(log n + log (log n)) does not apply
  (is (>= (nth-prime-upper-bound 1) 2))
  (is (>= (nth-prime-upper-bound 2) 3))
  (is (>= (nth-prime-upper-bound 3) 5))
  (is (>= (nth-prime-upper-bound 4) 7))
  (is (>= (nth-prime-upper-bound 5) 11)))
