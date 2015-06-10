(ns styling.app
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn widget [data]
  (om/component
   (html [:div "Hello world!"
          [:ul (for [n (range 1 10)]
                 [:li {:key n} n])]
          (html/submit-button "React!")])))

(defn main []
  (om/root widget {} {:target js/document.body}))
