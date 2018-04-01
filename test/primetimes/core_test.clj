(ns primetimes.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [primetimes.core :refer :all]))


(deftest row-formatting
  (is (= "   2 |    2   3   5 |"
        (table-row->stdout (Math/ceil
                               (+ 2
                                 (Math/log10
                                   (Math/pow 5 2)))) 2 [2 3 5]))))

