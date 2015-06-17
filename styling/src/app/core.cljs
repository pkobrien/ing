(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros [garden.def :refer [defcssfn defkeyframes defrule defstyles defstylesheet]])
  (:require [garden.arithmetic :refer [+ - * /]]
            [garden.color :as color :refer [hsl rgb]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :as u :refer [em pt px]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)


(def styles list) ; or use defstyles - "Convenience macro equivalent to `(def name (list styles*))`."

(def center-text {:text-align "center"})

;; (def clearfix
;;   ["&" {:*zoom 1}
;;    ["&:before" "&:after" {:content "\"\"" :display "table"}]
;;    ["&:after" {:clear "both"}]])

(def gutter (px 20))

(def alegreya ["Alegreya" "Baskerville" "Georgia" "Times" "serif"])
(def mono ["Inconsolata" "Menlo" "Courier" "monospace"])
(def sans ["\"Open Sans\"" "Avenir" "Helvetica" "sans-serif"])
(def sans-serif '[helvetica arial sans-serif])

(defrule article :article)
(defrule aside :aside)
(defrule body :body)
(defrule footer :footer)
(defrule header :header)

;(defrule page-body :body)
(defrule headings :h1 :h2 :h3)
(defrule sub-headings :h4 :h5 :h6)
(defrule on-hover :&:hover)
(defrule links :a:link)
(defrule active-links :a:active)
(defrule visited-links :a:visited)
(defrule ordered-list :ol)
(defrule unordered-list :ul)

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

(defn app-html [data]
  (html
   [:body
    [:header.box]
    [:article.box]
    [:aside.box]
    [:footer.box]
    ]))

(def app-stylesheet
  (css
   (body
    {:display "flex"}
    {:flex-flow "row"}
    {:justify-content "space-between"}
    {:align-items "stretch"})
   (header
    {:background-color (nth palette 0)}
    {:flex "0 0 auto"})
   (article
    {:background-color (nth palette 1)})
   (aside
    {:background-color (nth palette 2)})
   (footer
    {:background-color (nth palette 3)})
   [:.box
    {:min-height (px 100)}
    {:width (px 200)}]))

(defn app-root [data owner]
  (om/component (app-html data)))

(defn main []
  (om/root app-root app-state {:target js/document.body}))

(defn init []
  (set-title (:title init-state))
  (set-stylesheet app-stylesheet)
  (main))
