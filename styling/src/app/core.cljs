(ns app.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [garden.core :refer [css]]))

(defn page []
  (html [:div {:style {:color "red"}}
         [:h1 "Demonstrating Styling"]
         [:p "Hello, Styling!"]
         ]))

#_(defn page []
  (html [:head
         [:title "Demo"]
         ; [:style {:type "text/css"} (page-css)]
         ]
        [:body
         [:header.box]
         ]))

#_(defn page []
  (html [:div "Hello world!"
         [:ul (for [n (range 1 15)]
                [:li {:key n} n])]]))

(defn root [data]
  (om/component
   (page)))

(defn main []
  (om/root root {} {:target js/document.body}))

(main)
