(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros
   [freactive.macros :refer [rx]]
   [garden.def :refer [defcssfn defkeyframes defrule defstyles defstylesheet]])
  (:require
   [freactive.core :as r]
   [freactive.dom :as rdom]
   [freactive.animation :as animation]
   [garden.arithmetic :refer [+ - * /]]
   [garden.color :as color :refer [hsl rgb]]
   [garden.core :refer [css]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :as u :refer [em pt px]]
   [goog.dom :as gdom]
   [goog.dom.classes :as gclasses]
   [goog.events :as gevents])
  (:import [goog Timer]))

(enable-console-print!)

(rdom/enable-fps-instrumentation!)


;; -----------------------------------------------------------------------------
;; CSS Helpers

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


;; -----------------------------------------------------------------------------
;; Application State / Cursors

(defn- get-window-width [] (.-innerWidth js/window))

(defn- get-window-height [] (.-innerHeight js/window))

(defonce app-state
  (r/atom
   {:app {:name "Styling"
          :version "0.1.0"}
    :click-counter 0
    :current-time {:color "#ccc"
                   :value (js/Date.)}
    :mouse-pos {:x nil
                :y nil}
    :window {:width (get-window-width)
             :height (get-window-height)}
    }))

;; (defn load-state! []
;;   (reset! app-state (read-string (slurp "somefile"))))

;; (defn save-state []
;;   Better to write to a temp file and then rename the temp file.
;;   (spit "somefile" (prn-str @app-state)))

(def rc (partial r/cursor app-state))

(defonce rc-app-name
  (rc [:app :name]))

(defonce rc-app-version
  (rc [:app :version]))

(defonce rc-clicks
  (r/lens-cursor (rc :click-counter) identity #(inc %)))

(defonce rc-current-time
  (rc [:current-time :value]))

(defonce rc-mouse-pos
  (rc :mouse-pos))

(defonce rc-mouse-pos-x
  (rc [:mouse-pos :x]))

(defonce rc-mouse-pos-y
  (rc [:mouse-pos :y]))

(defonce rc-window
  (rc :window))

(defonce rc-window-h
  (rc [:window :height]))

(defonce rc-window-w
  (rc [:window :width]))


;; -----------------------------------------------------------------------------
;; Event Handlers

(defn- listen-for-mousemove! []
  (rdom/listen!
   js/window "mousemove"
   (fn [e]
     (assoc! rc-mouse-pos :x (.-clientX e) :y (.-clientY e)))))

(defn- listen-for-resize! []
  (rdom/listen!
   js/window "resize"
   (fn [e]
     (swap! rc-window assoc
            :width (get-window-width)
            :height (get-window-height)))))

(defonce init-app-listeners
  (do
    (listen-for-mousemove!)
    (listen-for-resize!)))

(defn on-button-click []
  (reset! rc-clicks))

(defonce on-interval-update-current-time!
  (js/setInterval
   #(reset! rc-current-time (js/Date.))
   1000))  ; every second (1000 ms)


;; -----------------------------------------------------------------------------
;; Stylesheet

(defn set-stylesheet! [stylesheet]
  (let [el (.createElement js/document "style")
        node (.createTextNode js/document stylesheet)]
    (.appendChild el node)
    (.appendChild (.-head js/document) el)))

(def app-stylesheet
  (css
   [:*
    {:box-sizing "border-box"}]
   [:audio
    {:width "100%"}]
   [:img :video
    {:height "auto" :max-width "100%"}]
   [:div :span
    {:box-sizing "border-box"
     :position "relative"
     :display "flex"
     :flex-direction "column"
     :align-items "stretch"
     :flex-shrink "0"
     :border "2 solid black"
     :margin "0"
     :padding "0"
     }]
   (body
    {:color "red"}
    )
   (header
    {:border {:width "1px" :style "dotted" :color "#333"}
     :color "blue"}
    )
   (main
    {:border {:width "2px" :style "dashed" :color "#666"}
     :color "red"
     :margin "1rem"
     :padding "1rem"}
    )
   (footer
    {:border {:width "1px" :style "dotted" :color "#333"}
     :color "orange"}
    )
   ))


;; -----------------------------------------------------------------------------
;; Title

(defn set-title! [title]
  (set! (.-title js/document) title))

(defn bind-title! [rw-title]
  (r/bind-attr* rw-title set-title! rdom/queue-animation))

(defn rw-app-title []
  (rx (str @rc-app-name " " @rc-window-w " by " @rc-window-h)))

;; (def title-binding (bind-title! (rw-app-title)))

;; (r/dispose title-binding)

(defn app-title []
  (str @rc-app-name " v" @rc-app-version))


;; -----------------------------------------------------------------------------
;; HTML

(defn app-html []
  [:div {:style "max-width: 20rem"}
   [:header
    [:h1 "Header Level 1"]
    [:h2 "Header Level 2"]
    ]
   [:main
    [:p "Date/Time: " (rx (str @rc-current-time))]
    [:p "Window size: " rc-window-w "px by " rc-window-h "px"]
    [:p "Mouse position: (" rc-mouse-pos-x ", " rc-mouse-pos-y ")"]
    [:p "Frames/second (60 max): " rdom/fps]
    [:p "Button Clicks: " rc-clicks " "
     [:button {:on-click on-button-click} "Click Me!"]]
    ]
   [:footer
    [:p "Footer content."]
    ]
   ])


;; -----------------------------------------------------------------------------
;; Init/Mount

(defn ^:export init []
  (set-stylesheet! app-stylesheet)
  (set-title! (app-title))
  (rdom/mount! "app" (app-html)))
