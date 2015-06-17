(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.color :as color :refer [hsl rgb]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :as u :refer [px pt]]))

(enable-console-print!)

(defonce init-state
  {:title "Reacting"
   :heading "Reacting with OM"
   :contacts
   [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
    {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
    {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
    {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
    {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
    {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]})

(defonce app-state (atom init-state))

(defn set-title [title]
  (set! (. js/document -title) title)
  js/document.title)

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
        (dom/span nil (display-name contact))
        (dom/button #js {:onClick (fn [e] (put! delete @contact))} "Delete")))))

(defn contacts-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [contact (<! delete)]
            (om/transact! data :contacts
              (fn [xs] (vec (remove #(= contact %) xs))))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/div nil
        (dom/h2 nil "Contact list")
        (apply dom/ul nil
          (om/build-all contact-view (:contacts data)
            {:init-state {:delete delete}}))))))

(defn app-html [data]
  (html
   [:div
    [:header
     [:h1 (:heading data)]
     ]
    [:aside
     [:p "This is within an aside..."]
     [:details
      [:summary "Click here for more details..."]
      [:p "Om is available " [:a {:href "https://github.com/omcljs/om"} "on GitHub"]]
      ]
     ]
    [:main
     [:p {:style {:color "red"}} "Hello, World!"]
     ]
    [:footer
     [:p "This is within the footer."]
     ]
    ]))

(defn app-root [data owner]
  (om/component (app-html data)))

(defn main []
  (om/root contacts-view app-state {:target (. js/document (getElementById "app"))}))

#_(defn main []
  (om/root app-root app-state {:target (. js/document (getElementById "app"))}))

(defn init []
  (set-title (:title init-state))
  (main))
