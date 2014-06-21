(defproject gameoflife "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2234"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.6.3"]
                 [org.clojure/core.match "0.2.1"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.2.3"]]
  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.4"]]}}

  :source-paths ["src" "test"]

  :cljsbuild { 
    :test-commands {"unit" ["phantomjs" 
                            :runner
                            "target/cljs/gameoflife.js"]}
    :builds [{:id "gameoflife"
              :source-paths ["src" "test"]
              :compiler {
                :output-to "target/cljs/gameoflife.js"
                :optimizations :simple
                :source-map "target/cljs/gameoflife.map"}}]})
