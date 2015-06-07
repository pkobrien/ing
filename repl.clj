(require 'cljs.build.api)
(require 'cljs.repl)
(require 'cljs.repl.browser)

(cljs.build.api/build "src"
  {:main 'gardening.core
   :output-to "out/gardening.js"
   :verbose true})

(cljs.repl/repl (cljs.repl.browser/repl-env)
  :watch "src"
  :output-dir "out"
  :repl-verbose true)
