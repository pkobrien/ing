(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros
   [freactive.macros :refer [rx]]
   [garden.def :refer [defcssfn defkeyframes defrule defstyles defstylesheet]])
  (:require
   [freactive.core :as r]
   [freactive.dom :as dom]
   [freactive.animation :as animation]
   [garden.arithmetic :refer [+ - * /]]
   [garden.color :as color :refer [hsl rgb]]
   [garden.core :refer [css]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :as u :refer [em pt px]]))

(enable-console-print!)

(dom/enable-fps-instrumentation!)


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
   {:app-name "Styling"
    :counter 0
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

(defonce rc-current-time-value
  (r/cursor app-state [:current-time :value]))

(defonce rc-mouse-pos
  (r/cursor app-state :mouse-pos))

(defonce rc-mouse-pos-x
  (r/cursor app-state [:mouse-pos :x]))

(defonce rc-mouse-pos-y
  (r/cursor app-state [:mouse-pos :y]))

(defonce rc-window
  (r/cursor app-state :window))

(defn- listen-to-mousemove! []
  (dom/listen!
   js/window "mousemove"
   (fn [e]
     (assoc! rc-mouse-pos :x (.-clientX e) :y (.-clientY e)))))

(defn- listen-to-resize! []
  (dom/listen!
   js/window "resize"
   (fn [e]
     (swap! rc-window assoc
            :width (get-window-width)
            :height (get-window-height)))))

(defonce init-app-listeners
  (do
    (listen-to-mousemove!)
    (listen-to-resize!)))

(defonce on-interval-update-current-time-value!
  (js/setInterval
   #(reset! rc-current-time-value (js/Date.))
   1000))  ; every second (1000 ms)


;; -----------------------------------------------------------------------------
;; Local State / Cursors

(defonce clicks (r/atom 0))

(defonce rc-clicks (r/lens-cursor clicks str #(inc %1)))

(defn on-button-click []
  (reset! rc-clicks))


;; -----------------------------------------------------------------------------
;; HTML

(defn set-stylesheet! [stylesheet]
  (let [el (.createElement js/document "style")
        node (.createTextNode js/document stylesheet)]
    (.appendChild el node)
    (.appendChild (.-head js/document) el)))

(defn set-title! [title]
  (set! (.-title js/document) title))

#_(def app-stylesheet
  (css
   (body
    )
   (header
    )
   (main
    )
   (footer
    )
   ))

(defn app-title []
  (:app-name @app-state))

(defn app-html []
  [:div
   [:header
    [:h1 "Header Level 1"]
    [:h2 "Header Level 2"]
    ]
   [:main
    [:p "Main content goes here."]
    [:button {:on-click on-button-click} "Click Me!"]
    ]
   [:footer
    [:p "Footer content."]
    [:p "Mouse position: " (rx (str "(" (:x @rc-mouse-pos) ", " (:y @rc-mouse-pos) ")"))]
    [:p "Mouse position: " (str "(" (:x @rc-mouse-pos) ", " (:y @rc-mouse-pos) ")")]  ; NOT working
    [:p "Mouse position: " (str "(" (:x rc-mouse-pos) ", " (:y rc-mouse-pos) ")")]  ; NOT working
    [:p "Mouse pos: " (str (vals @rc-mouse-pos))]
    [:p "Mouse X: " rc-mouse-pos-x]
    [:p "Mouse Y: " rc-mouse-pos-y]
    [:p "Window size is: " (rx (str (:width @rc-window) " by " (:height @rc-window)))]
    [:p "Time: " (rx (str @rc-current-time-value))]
    [:p "Time: " (str @rc-current-time-value)]  ; NOT working
    [:p "Time: " (str rc-current-time-value)]  ; NOT working
    [:p "Clicks: " (rx @rc-clicks)]
    [:p "Clicks: " rc-clicks]
    [:p "Frames/second: " (rx @dom/fps) " (60 maximum)"]
    [:p "Frames/second: " dom/fps " (60 maximum)"]
    ]
   ])

(defn init []
;  (set-stylesheet! app-stylesheet)
  (set-title! (app-title)))

(dom/mount! "app" (app-html))
