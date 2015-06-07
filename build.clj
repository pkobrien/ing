(require 'cljs.build.api)

(cljs.build.api/build "src"
  {:main 'gardening.core
   :output-to "out/gardening.js"})
