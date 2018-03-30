(ns primetimes.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clojure.pprint :as pp]
    [clojure.tools.cli :refer [parse-opts] :as cli]))

(declare pln xpln now row-fmt println-prime-times first-n-primes)

(def primetimes-cli
  [["-p" "--padding CELLPADDING" "How many extra spaces should be added to widen each cell."
    :id :cell-padding
    :parse-fn #(Integer/parseInt %)
    :default 2
    :validate [#(>= % 0) "Padding must be at least zero."]]

   ["-h" "--help"]])

(declare prime-times-to-stdout)
#_
(-main "8" )

(defn -main [& cli-args]
  (let [input (cli/parse-opts cli-args primetimes-cli)
        {:keys [options arguments summary errors]} input
        {:keys [cell-padding help]} options
        nprimes (let [prime-ct (first arguments)]
                  (cond
                    (nil? prime-ct) 5
                    (integer? prime-ct) prime-ct
                    (string? prime-ct)
                    (try
                      (Integer/parseInt prime-ct)
                      (catch Exception e
                        (do (println (str "Invalid number of primes: " (.getMessage e)))
                            nil)))
                    :default
                    nil))]
    (when nprimes
      (cond
        errors (doseq [e errors]
                 (println e))

        help (println "\nUsage:\n\n    primetimes <prime-ct> options*\n\n"
               "Options:\n" (subs summary 1))

        :default (prime-times-to-stdout nprimes cell-padding)))))

;; --- report output ----------------------------

(declare table-row-to-stdout)

(defn prime-times-to-stdout [prime-ct cell-padding]
  (let [primes (first-n-primes prime-ct)
        max-places (Math/ceil
                     (Math/log10
                       (Math/pow (last primes) 2)))
        cell-width (+ (max cell-padding 1) max-places)
        header (table-row-to-stdout cell-width "X" primes)
        divider (pp/cl-format nil "~v,,,'-a|~v,,,'-a|"
                  (inc cell-width) ""
                  (- (count header) cell-width 3) "")]

    (println divider)
    (println header)
    (println divider)

    (loop [n 1
           pp nil
           [p & rp] primes]
      (when p
        (let [prods (map #(* p %) primes)]
          (println (table-row-to-stdout cell-width p prods))
          (when (zero? (mod n 5))
            (println divider))
          (recur (inc n) p rp))))

    (println divider)))

(defn table-row-to-stdout
  "Common Lisp format is a Turing comlete DSL and
  on a task like this is invaluable.

  http://www.lispworks.com/documentation/lw50/CLHS/Body/f_format.htm"

  [cell-width row-prime row]

  (pp/cl-format nil
    "~v@a | ~{~vd~} |" cell-width row-prime
    (interleave
      (repeat cell-width)
      row)))

;; --- Paul Cowan's Sieve of Erasthones ---

(defn calc-limit [n]
  (let [log (Math/log n)
        loglog (Math/log log)
        logsum (+ log loglog)]
    (-> n (* logsum) int (+ 3))))

(defn primes [n]
  (let [max-factor (Math/sqrt n)
        sieve (boolean-array n true)]
    (loop [i 2]
      (when (<= i max-factor)
        (when (aget sieve i)
          (loop [j (* i 2)]
            (when (< j n)
              (aset sieve j false)
              (recur (+ j i)))))
        (recur (inc i))))
    (filter #(aget sieve %) (range 2 n))))

(defn first-n-primes [n]
  (case n
    0 []
    1 [2]
    2 [2 3]
    (take n (primes (calc-limit n)))))

;; --- utils -----------------------------

(defn pln [& args]
  (locking *out*
    (println (str/join " " args))))

(defn xpln [& args])

(defn now []
  (System/currentTimeMillis))