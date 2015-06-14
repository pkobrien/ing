(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.color :as color :refer [hsl rgb]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :as u :refer [em pt px]]))

(enable-console-print!)

(defonce init-state {:title "Styling"})

(defonce app-state (atom init-state))

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

(defn body []
  (html [:body
         [:header.box]
         [:article.box]
         [:aside.box]
         [:footer.box]
         ]))

(def stylesheet
  (css [:body
        {:display "flex"}
        {:flex-flow "row"}
        {:justify-content "space-between"}
        {:align-items "stretch"}]
       [:header
        {:background-color (nth palette 0)}
        {:flex "0 0 auto"}]
       [:article
        {:background-color (nth palette 1)}]
       [:aside
        {:background-color (nth palette 2)}]
       [:footer
        {:background-color (nth palette 3)}]
       [:.box
        {:min-height (px 100)}
        {:width (px 200)}]))

(defn root [data]
  (om/component
   (body)))

(defn main []
  (om/root root app-state {:target js/document.body}))

(defn init []
  (set-title (:title init-state))
  (set-stylesheet stylesheet)
  ;(prn stylesheet)
  (main))
