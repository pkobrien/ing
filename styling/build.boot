(set-env!
 :source-paths   #{"src"}
 :resource-paths #{"html"}
 :dependencies '[[adzerk/boot-cljs "0.0-3269-2" :scope "test"]
                 [org.omcljs/om "0.8.8"]
                 [sablono "0.3.4"]])

(require
 '[adzerk.boot-cljs :refer [cljs]])

(deftask build []
  (comp (speak)
        (cljs)))
