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

(defonce init-state
  {:title "Something"
   :heading "Demonstrating Something"})

(defonce app-state (atom init-state))

(defn set-title [title]
  (set! (. js/document -title) title)
  js/document.title)

#_(defn app-html [data]
  (html
   [:body
    [:header {:role "banner"}
     [:nav {:role "navigation"}]
     ]
    [:aside {:role "complementary"}
     ]
    [:main
     ]
    [:footer {:role "contentinfo"}
     ]
    ]))

(defn app-html [data]
  (html
   [:body
    [:header
     [:h1 (:heading data)]
     ]
    [:aside
     [:p {:style {:color "red"}} "This is within an aside..."]
     [:details
      [:summary "Click here for more details..."]
      [:p "Om is " [:a {:href "https://github.com/omcljs/om"} "available on GitHub"]]
      ]
     ]
    [:main
     [:p {:style {:color "green"}} "Hello, World!"]
     ]
    [:footer
     [:p "This is within the footer."]
     ]
    ]))

(defn app-root [data owner]
  (om/component (app-html data)))

(defn main []
  (om/root app-root app-state {:target js/document.body}))

#_(defn main []
  (om/root app-root app-state {:target (. js/document (getElementById "app"))}))

(defn init []
  (set-title (:title init-state))
  (main))
