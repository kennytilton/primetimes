(ns primetimes.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clojure.pprint :as pp]
    [clojure.tools.cli :refer [parse-opts] :as cli]
    [primetimes.prime-numbers :refer :all]))

(def primetimes-cli
  [["-p" "--padding CELLPADDING" "How many extra spaces should be added to widen each cell."
    :id :cell-padding
    :parse-fn #(Integer/parseInt %)
    :default 2
    :validate [#(>= % 0) "Padding must be at least zero."]]

   ["-h" "--help"]])

(declare prime-times->stdout)

#_(-main "-h")

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
                        (println (str "Invalid number of primes: " (.getMessage e)))))
                    :default
                    nil))]

    (when nprimes
      (cond
        errors (doseq [e errors]
                 (println e))

        help (println "\nFunctionality:\n\n"
        "  Print a times table of the first N primes to STDOUT.\n\n"
               "Usage:\n\n    primetimes [prime-ct] options*\n\n"
               "prime-ct: Must be at least zeo. Defaults to five (5)\n\n"
               "Options:\n" summary "\n")

        :default (when (>= nprimes 1)
                   (prime-times->stdout nprimes cell-padding))))))

;; --- report output ----------------------------

(declare table-row->stdout)

(defn prime-times->stdout [prime-ct cell-padding]
  (let [primes (first-n-primes prime-ct)

        ;; places as in "tens place". How wide is a number?
        max-places (Math/ceil
                     (Math/log10
                       (Math/pow (last primes) 2)))
        cell-width (+ (max cell-padding 1) max-places)

        ;; we'll use this to measure de facto the table width
        table-header (table-row->stdout cell-width "X" primes)

        table-divider (pp/cl-format nil "~v,,,'-a|~v,,,'-a|"
                  (inc cell-width) ""
                  (- (count table-header) cell-width 3) "")]

    (println table-divider)
    (println table-header)
    (println table-divider)

    (loop [row-n 1
           [p & rp] primes]
      (when p
        (let [prods (map #(* p %) primes)]
          ;; the beef
          (println (table-row->stdout cell-width p prods))
          ;; tables are easier to parse with a divider every so many rows:
          (when (zero? (mod row-n 5))
            (println table-divider))

          (recur (inc row-n) rp))))

    (println table-divider)))

(defn table-row->stdout
   "Common Lisp format is a Turing comlete DSL and on a task like
    this is great albeit obscure and slower than painfully hard-coded
    formatting. Revisit choice if slow.

    Its doc: http://www.lispworks.com/documentation/lw50/CLHS/Body/f_format.htm"

  [cell-width row-prime table-values]

  (pp/cl-format nil "~v@a | ~{~vd~} |"
    cell-width
    row-prime
    (interleave
      (repeat cell-width)
      table-values)))
