(defproject primetimes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [com.hypirion/primes "0.2.2"]
                 [com.taoensso/tufte "1.4.0"]]
  :main ^:skip-aot primetimes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev  {:plugins [[lein-binplus "0.6.4"]]}}
  :bin {:name "primetimes"
        :bin-path "./bin"})
