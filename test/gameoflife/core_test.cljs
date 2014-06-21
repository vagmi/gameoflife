(ns gameoflife.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [gameoflife.core :as g]
            cemerick.cljs.test))

(deftest somewhat-less-wat
  (is (= 2 2)))

