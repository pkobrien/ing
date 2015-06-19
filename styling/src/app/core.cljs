(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros
   [freactive.macros :refer [rx]]
   [garden.def :refer [defcssfn defkeyframes defrule defstyles defstylesheet]])
  (:require
   [freactive.core :as r]
   [freactive.dom :as dom]
   [freactive.animation :as animation]
   [garden.arithmetic :refer [+ - * /]]
   [garden.color :as color :refer [hsl rgb]]
   [garden.core :refer [css]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :as u :refer [em pt px]]))

(enable-console-print!)

(dom/enable-fps-instrumentation!)


;; (def center-text {:text-align "center"})

;; (def clearfix
;;   ["&" {:*zoom 1}
;;    ["&:before" "&:after" {:content "\"\"" :display "table"}]
;;    ["&:after" {:clear "both"}]])

;; (def gutter (px 20))

;; (def alegreya ["Alegreya" "Baskerville" "Georgia" "Times" "serif"])
;; (def mono ["Inconsolata" "Menlo" "Courier" "monospace"])
;; (def sans ["\"Open Sans\"" "Avenir" "Helvetica" "sans-serif"])
;; (def sans-serif '[helvetica arial sans-serif])

(defrule article :article)
(defrule aside :aside)
(defrule body :body)
(defrule footer :footer)
(defrule header :header)
(defrule main :main)

(defrule headings :h1 :h2 :h3)
(defrule sub-headings :h4 :h5 :h6)

(defrule ordered-list :ol)
(defrule unordered-list :ul)

(defrule active-links :a:active)
(defrule links :a:link)
(defrule on-hover :&:hover)
(defrule visited-links :a:visited)

;; (defrule center :div.center)
;; (defrule top :section#top)
;; (defrule main :section#main)
;; (defrule sidebar :section#sidebar)

;; (defn dangerous
;;   ([component content]
;;    (dangerous component nil content))
;;   ([component props content]
;;    [component (assoc props :dangerouslySetInnerHTML {:__html content})]))

(def palette
  (let [base-color (hsl 0 100 50)]
    (color/shades base-color)))

(defn set-stylesheet [stylesheet]
  (let [el (.createElement js/document "style")
        node (.createTextNode js/document stylesheet)]
    (.appendChild el node)
    (.appendChild (.-head js/document) el)
    el))

(defn set-title [title]
  (set! (. js/document -title) title)
  js/document.title)


;; (defonce update-counter
;;   (js/setInterval
;;    #(dispatch [:update-counter])
;;    (* 4 1000)))  ; every so often

;; (defonce update-current-time-value
;;   (js/setInterval
;;    #(dispatch [:update-current-time-value])
;;    1000))  ; every second (1000 ms)


;; (defn save-state []
;;   Better to write to a temp file and then rename the temp file.
;;   (spit "somefile" (prn-str @app-state)))

;; (defn load-state []
;;   (reset! app-state (read-string (slurp "somefile"))))

(defonce app-state
  (r/atom
   {:app-name "Styling"
    :counter 0
    :current-time {:color "#ccc"
                   :value (js/Date.)}
    :mouse-pos {:x nil
                :y nil}
    }))

(defonce rc-mouse-pos (r/cursor app-state :mouse-pos))

(defn listen-to-mousemove! []
  (dom/listen!
   js/window "mousemove"
   (fn [e]
     (swap! rc-mouse-pos assoc :x (.-clientX e) :y (.-clientY e)))))

(defonce app-init
  (do
    (listen-to-mousemove!)))

#_(def app-stylesheet
  (css
   (body
    )
   (header
    )
   (main
    )
   (footer
    )
   ))

(defn app-html []
  [:div
   [:header
    [:h1 "Header Level 1"]
    ]
   [:main
    [:p "Main content goes here."]
    ]
   [:footer
    [:p "Footer content. Mouse position: " (rx (str "(" (:x @rc-mouse-pos) ", " (:y @rc-mouse-pos) ")"))]
    ]
   ])

(defn init []
  (set-title (:app-name @app-state))
;  (set-stylesheet app-stylesheet)
  (dom/mount! "app" (app-html)))
