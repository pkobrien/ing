(ns app.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [garden.core :refer [css]]))

(enable-console-print!)

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

(def styles
  (css [:h1 :h2 :h3 {:font-weight "none"}]))

(defn root [data]
  (om/component
   (page)))

(defn main []
  (om/root root {} {:target js/document.body}))

(defn init []
  (set-title "Styling")
  (set-styles styles)
  (prn styles)
  (main))
