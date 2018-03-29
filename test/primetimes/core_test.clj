(ns primetimes.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [primetimes.core :refer :all]
            [com.hypirion.primes :as p]))


#_
    (let [n 1000000]
      [(time (nth-prime n))
       (time (p/get (dec n)))])


