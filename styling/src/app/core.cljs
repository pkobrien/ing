(ns app.core
  (:refer-clojure :exclude [atom + - * /])
  (:require
   [freactive.core :as r :refer [atom cursor]]
   [freactive.dom :as dom]
   [freactive.animation :as animation]
   [garden.arithmetic :refer [+ - * /]]
   [garden.color :as color :refer [hsl rgb]]
   [garden.core :refer [css]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :as u :refer [em pt px]])
  (:require-macros
   [freactive.macros :refer [rx]]
   [garden.def :refer [defcssfn defkeyframes defrule defstyles defstylesheet]]))

(enable-console-print!)


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


(defonce init-state {:title "Styling"})

(defonce app-state (atom init-state))

(defn app-html []
  [:body
   [:header
    [:h1 "Header Level 1"]
    ]
   [:main
    [:p "Main content goes here."]
    ]
   [:footer
    [:p "Footer content."]
    ]
   ])

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

(defn init []
  (set-title (:title init-state))
;  (set-stylesheet app-stylesheet)
  (dom/mount! js/document.body (app-html)))
