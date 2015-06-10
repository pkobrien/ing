(set-env!
 :source-paths   #{"src/styling"}
 :resource-paths #{"./"}
 :dependencies '[[adzerk/boot-cljs "0.0-3269-2" :scope "test"]
                 [adzerk/boot-cljs-repl "0.1.10-SNAPSHOT" :scope "test"]
                 [adzerk/boot-reload "0.2.6" :scope "test"]
                 [pandeiro/boot-http "0.6.3-SNAPSHOT" scope "test"]
                 [org.omcljs/om "0.8.8"]
                 [sablono "0.3.4"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask build []
  (comp (speak)
        (cljs)))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :unified-mode true
                       :source-map true}
                 reload {:on-jsload 'styling.app/main})
  identity)

(deftask dev
  []
  (comp (development)
        (run)))
