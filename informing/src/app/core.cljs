(ns app.core
  (:refer-clojure :exclude [+ - * /])
  (:require-macros
   [freactive.macros :refer [rx]])
  (:require
   [app.cuss :as cuss]
   [app.poly :as poly]
   [freactive.core :as r]
   [freactive.dom :as rdom]
   [freactive.animation :as animation]
   [garden.arithmetic :refer [+ - * /]]
   [garden.color :as color :refer [hsl rgb]]
   [garden.core :refer [css]]
   [garden.units :as u :refer [em pt px]]
   [goog]))

(enable-console-print!)

(rdom/enable-fps-instrumentation!)


;; -----------------------------------------------------------------------------
;; State (omni- : all : in all ways, places, etc. : without limits
;;        http://www.merriam-webster.com/dictionary/omni-)

(defonce omni-state
  (r/atom
   {:app {:name "Informing"
          :version "0.1.0"
          }
    :dom {:document-height nil
          :document-scroll {:x nil :y nil}
          :viewport {:width nil :height nil}
          }
    :env {:mouse-pos {:x nil :y nil}
          :time nil
          }
    :gui {:click-count 0
          }
    }))

;; (defn load-state! []
;;   (reset! omni-state (read-string (slurp "somefile"))))

;; (defn save-state []
;;   Better to write to a temp file and then rename the temp file.
;;   (spit "somefile" (prn-str @omni-state)))


;; -----------------------------------------------------------------------------
;; Cursor Creators (Omni's little helpers)

(def cc (partial r/cursor omni-state))

(defonce cc-app (partial r/cursor (cc :app)))

(defonce cc-dom (partial r/cursor (cc :dom)))

(defonce cc-env (partial r/cursor (cc :env)))

(defonce cc-gui (partial r/cursor (cc :gui)))


;; -----------------------------------------------------------------------------
;; Reactive Cursors (Cursors with watchers that, um, react to mutations, magically)

(defonce rc-app-name
  (cc-app [:name]))

(defonce rc-app-version
  (cc-app [:version]))

(defonce rc-dom-document-h
  (cc-dom [:document-height]))

(defonce rc-dom-document-scroll-x
  (cc-dom [:document-scroll :x]))

(defonce rc-dom-document-scroll-y
  (cc-dom [:document-scroll :y]))

(defonce rc-dom-viewport-h
  (cc-dom [:viewport :height]))

(defonce rc-dom-viewport-w
  (cc-dom [:viewport :width]))

(defonce rc-env-mouse-pos
  (cc-env [:mouse-pos]))

(defonce rc-env-mouse-pos-x
  (cc-env [:mouse-pos :x]))

(defonce rc-env-mouse-pos-y
  (cc-env [:mouse-pos :y]))

(defonce rc-env-time
  (cc-env [:time]))

(defonce rc-gui-click-count
  (r/lens-cursor (cc-gui [:click-count]) identity inc))


;; -----------------------------------------------------------------------------
;; State Mutators (reset! swap! assoc! dissoc! r/assoc-in! r/update! r/update-in!)

(defn mutate-dom-window-size! [w h]
  (reset! rc-dom-document-h (poly/get-document-height))
  (reset! rc-dom-document-scroll-x (poly/get-document-scroll-x))
  (reset! rc-dom-document-scroll-y (poly/get-document-scroll-y))
  (reset! rc-dom-viewport-h h)
  (reset! rc-dom-viewport-w w))

(defn mutate-env-time! []
  (reset! rc-env-time (poly/now)))

(defn mutate-env-mouse-pos! [x y]
  (assoc! rc-env-mouse-pos :x x :y y))

(defn mutate-gui-click-count! []
  (reset! rc-gui-click-count))


;; -----------------------------------------------------------------------------
;; Event Handlers (On and on and on, over and over again...)

(defn on-dom-window-load [e]
  (mutate-dom-window-size! (poly/get-viewport-width) (poly/get-viewport-height)))

(defn on-dom-window-resize [w h]
  (mutate-dom-window-size! w h))

(defn on-env-mouse-move [e]
  (mutate-env-mouse-pos! (.-clientX e) (.-clientY e)))

(defn on-env-time-interval []
  (mutate-env-time!))

(defn on-gui-button-click [e]
  (mutate-gui-click-count!))


;; -----------------------------------------------------------------------------
;; Event Listeners (Shh! Did you hear that? Something's happening somewhere...)

(defonce listen-for-dom-window-load
  (rdom/listen! js/window "load" on-dom-window-load))

(defonce listen-for-dom-window-resize
  (poly/listen-for-viewport-resize! on-dom-window-resize))

(defonce listen-for-env-mouse-move
  (rdom/listen! js/window "mousemove" on-env-mouse-move))


;; -----------------------------------------------------------------------------
;; Timers (Hickory, dickory, dock. The mouse ran up the clock.
;;         The clock struck one, the mouse ran down, hickory, dickory, dock.)

(defonce interval-for-env-time
  (js/setInterval on-env-time-interval 1000))  ; every second (1000 ms)


;; -----------------------------------------------------------------------------
;; Style ("You gotta have style. It helps you get down the stairs.
;;        It helps you get up in the morning. It's a way of life")

(defn get-stylesheet []
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
   (cuss/body
    {:color "red"}
    )
   (cuss/header
    {:border {:width "1px" :style "dotted" :color "#333"}
     :color "blue"}
    )
   (cuss/main
    {:border {:width "2px" :style "dashed" :color "#666"}
     :color "red"
     :margin "1rem"
     :padding "1rem"}
    )
   (cuss/footer
    {:border {:width "1px" :style "dotted" :color "#333"}
     :color "orange"}
    )
   ))


;; -----------------------------------------------------------------------------
;; Title ("Titles are but nicknames, and every nickname is a title")

(defn get-title []
  (str @rc-app-name " v" @rc-app-version))

;; (defn bind-title! [rw-title]
;;   (r/bind-attr* rw-title poly/set-title! rdom/queue-animation))

;; (defn rw-app-title []
;;   (rx (str @rc-app-name " " @rc-viewport-w " by " @rc-viewport-h)))

;; (def title-binding (bind-title! (rw-app-title)))

;; (r/dispose title-binding)


;; -----------------------------------------------------------------------------
;; HTML (We're almost there now. So close. The reactive gui of our dreams...)

(defn get-html []
  [:div {:style "max-width: 20rem"}
   [:header
    [:h1 "Header Level 1"]
    [:h2 "Header Level 2"]
    ]
   [:main
    [:p "goog.global " (str goog.global)]
    [:p "goog.global.COMPILED " goog.global.COMPILED]
    [:p "goog.DEBUG " goog.DEBUG]
    [:p "goog.LOCALE " goog.LOCALE]
    [:p "goog.TRUSTED_SITE " goog.TRUSTED_SITE]
    [:p "goog.STRICT_MODE_COMPATIBLE " goog.STRICT_MODE_COMPATIBLE]
    [:p "goog.DISALLOW_TEST_ONLY_CODE " goog.DISALLOW_TEST_ONLY_CODE]
    [:p "goog.ENABLE_CHROME_APP_SAFE_SCRIPT_LOADING " goog.ENABLE_CHROME_APP_SAFE_SCRIPT_LOADING]
    [:p "goog.now() " (goog/now)]
    [:p "Date/Time " (rx (str @rc-env-time))]
    [:p "Viewport size " rc-dom-viewport-w "px by " rc-dom-viewport-h "px"]
    [:p "Document height " rc-dom-document-h "px"]
;    [:p "Document scroll " rc-dom-document-scroll-x " by " rc-dom-document-scroll-y]
    [:p "Mouse position " "(" rc-env-mouse-pos-x ", " rc-env-mouse-pos-y ")"]
    [:p "Frames/second (60 max) " rdom/fps]
    [:p "Button Clicks " rc-gui-click-count " "
     [:button {:on-click on-gui-button-click} "Click Me!"]]
    ]
   [:footer
    [:p "Footer content."]
    ]
   ])


;; -----------------------------------------------------------------------------
;; Init/Mount (Let's get this party started!!!)

(defn ^:export init []
  (poly/set-stylesheet! (get-stylesheet))
  (poly/set-title! (get-title))
  (rdom/mount! "app" (get-html)))
