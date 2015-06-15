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

(defonce init-state {:title "Boxing"})

(defonce app-state (atom init-state))

(defn set-title [title]
  (set! (. js/document -title) title)
  js/document.title)

(defn body []
  (html [:body
         [:h1 "Demonstrating Boxing"]
         [:p {:style {:color "red"}} "Hello, World!"]
         ]))

(defn root [data]
  (om/component
   (body)))

(defn main []
  (om/root root app-state {:target js/document.body}))

(defn init []
  (set-title (:title init-state))
  (main))
