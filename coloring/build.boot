(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"../html"}
  :dependencies '[[adzerk/boot-cljs           "0.0-3269-2"      :scope "test"]
                  [adzerk/boot-cljs-repl      "0.1.10-SNAPSHOT" :scope "test"]
                  [adzerk/boot-reload         "0.2.6"           :scope "test"]
                  [boot-cljs-test/node-runner "0.1.0"           :scope "test"]
                  [pandeiro/boot-http         "0.6.3-SNAPSHOT"  :scope "test"]
                  [org.clojure/clojurescript "0.0-3308"]
                  [org.omcljs/om "0.8.8"]
                  [sablono "0.3.4"]
                  [garden "1.2.5"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload :refer [reload]]
  '[boot-cljs-test.node-runner :refer [cljs-test-node-runner run-cljs-test]]
  '[pandeiro.boot-http :refer [serve]])

(task-options!
  reload {:on-jsload 'app.core/init}
  serve {:dir "target/"})

(deftask build []
  (set-env! :source-paths #{"src"})
  (comp (cljs :optimizations :advanced)))

(deftask dev []
  (comp (serve)
        (watch)
        (speak)
        (reload)
        (cljs-repl)
        (cljs :source-map true :optimizations :none)))

(deftask tst []
  (set-env! :source-paths #{"src" "test"})
  (comp (serve)
        (watch)
        (speak)
        (reload)
        (cljs-repl)
        (cljs-test-node-runner :namespaces '[app.test])
        (cljs :source-map true :optimizations :none)
        (run-cljs-test)))
