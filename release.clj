(require 'cljs.build.api)

(cljs.build.api/build "src"
  {:output-to "out/gardening.js"
   :optimizations :advanced})

(System/exit 0)
