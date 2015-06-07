(require 'cljs.build.api)

(cljs.build.api/watch "src"
  {:main 'gardening.core
   :output-to "out/gardening.js"})
