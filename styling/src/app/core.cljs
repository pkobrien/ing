(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.color :as color :refer [hsl rgb]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :as u :refer [px pt]]))

(enable-console-print!)

(defonce init-state {:title "Styling"})

(defonce app-state (atom init-state))

(defn set-styles [styles]
  (let [el (.createElement js/document "style")
        node (.createTextNode js/document styles)]
    (.appendChild el node)
    (.appendChild (.-head js/document) el)
    el))

(defn set-title [title]
  (set! (. js/document -title) title)
  js/document.title)

(defn page []
  (html [:div
         [:h1 "Demonstrating Styling"]
         [:p {:style {:color "red"}} "Hello, Styling!"]
         [:header.box]
         ]))

(defn foo [a b]
  (+ a b))

(defn bar [x y]
  (* x y 42))

(defn baz [a b]
  (+ a b 42))

(def styles
  (css [:h1 :h2 {:font-weight "none"}]))

(defn root [data]
  (om/component
   (page)))

(defn main []
  (om/root root app-state {:target js/document.body}))

(defn init []
  (set-title (:title init-state))
  (set-styles styles)
  (prn styles)
  (main))
