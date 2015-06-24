(ns app.macros
  (:require
   [garden.stylesheet :refer [at-media]]))

;; -----------------------------------------------------------------------------
;; CSS Helpers

(defmacro defbreakpoint [name media-params]
  `(defn ~name [& rules#]
     (at-media ~media-params
       [:& rules#])))
