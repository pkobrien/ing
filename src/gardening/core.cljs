(ns gardening.core
  (:require [clojure.browser.repl :as repl]))

(enable-console-print!)

(defonce conn (repl/connect "http://localhost:9000/repl"))

(println "Hello, Gardener!")



;; -----------------------------------------------------------------------------
;; REPL Exploring

#_(defn foo [a b]
  (+ a b))

;; in the repl do the following:

;; (require '[gardening.core :as g] :reload)
;; (g/foo 2 3)

;; (js/alert "I am an evil side-effect")

