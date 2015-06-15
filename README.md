# ing
Immutable Nonsensical Goodies



REPL Stuff

Canonical Workflow:

```
1) Startup
> boot repl -c
boot.user=> (start-repl)
cljs.user=> (require '[app.core])
cljs.user=> (in-ns 'app.core)
app.core=>

2) Make a change to core.cljs that requires a reload
app.core=> (require '[app.core] :reload)

3) Shutdown
app.core=> :cljs/quit
boot.user=> (quit)
```

Example Session:

```
> boot repl -c
boot.user=> (start-repl)
cljs.user=> (js/alert "This is a test of the emergency broadcasting system.")
cljs.user=> (require '[app.core :as app])
cljs.user=> (app/foo 3 4)
7
cljs.user=> (in-ns 'app.core)
app.core=> (foo 3 4)
7
app.core=> :cljs/quit
boot.user=> (quit)
Bye for now!
```
