(ns primetimes.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [primetimes.core :refer :all]
            [com.hypirion.primes :as p]))

;; The prime generation code is a Sieve of Erasthones implementation
;; by Paul Cowan, a self-proclaimed Cattle Rustler:
;;
;;   http://www.thesoftwaresimpleton.com/blog/2015/02/07/primes/
;;
;; we test against https://github.com/hypirion/primes

(defn nth-prime [n]
  (cond
    (= n 1) 2
    (= n 2) 3
    :else (last (take n (primes (calc-limit n))))))

(deftest rustler-works
  (testing "Get nth prime"
    (let [n 3]
      (is (= 5
            (nth-prime n)
            (p/get (dec n))))
      (let [n 10]
        (is (= 29
              (nth-prime n)
              (p/get (dec n)))))
      (let [n 30]
        (is (= (nth-prime n)
              (p/get (dec n)))))))
  (testing "Get first N primes"
    (let [n 10]
      (is (= [2 3 5 7 11 13 17 19 23 29]
            (first-n-primes n)
            (p/take n)))

      (let [n 30]
        (is (= (first-n-primes n)
              (p/take n)))))))

;; OK, a half hour later we have learned that
;; hypirion counts from zero and rustler from 1.
;; And! We caught a bug I introduced hacking on
;; Rustler...I love TDD!

(deftest row-formatting
  (is (= "   2 |    2   3   5 |"
        (table-row-to-stdout (Math/ceil
                   (+ 2
                     (Math/log10
                       (Math/pow 5 2)))) 2 [2 3 5]))))

;; It is not really a test because I run the code, copy the
;; result into the test, but it makes development quicker
;; just hitting F2 in IntelliJ






