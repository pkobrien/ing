(ns app.poly
  (:require
   [cljs.core :as cljs]
   [goog.dom :as dom]
   [goog.dom.classes :as classes]
   [goog.dom.forms :as forms]
   [goog.dom.xml :as xml]
   [goog.events :as events]
   [goog.net.Jsonp]
   [goog.object :as gobj]
   [goog.string :as gstring]
   [goog.style :as style]
   [goog.Uri])
  (:import [goog Timer]
           [goog.events EventType]))

(enable-console-print!)


;; -----------------------------------------------------------------------------
;; DOM
;; https://github.com/google/closure-library/blob/master/closure/goog/dom/dom.js

(defn get-document []
  (dom/getDocument))

(defn get-element [id]
  (dom/getElement (cljs/name id)))

(defn get-required-element [id]
  (dom/getRequiredElement (cljs/name id)))

(defn get-elements-by-tag-name-and-class
  ([tag-name]
   (dom/getElementsByTagNameAndClass (cljs/name tag-name)))
  ([tag-name class-name]
   (dom/getElementsByTagNameAndClass (cljs/name tag-name) (cljs/name class-name))))


;; -----------------------------------------------------------------------------
;; DOM Synonyms

(def by-id get-element)
(def find-by-id get-element)
(def get-element-by-id get-element)

;; -----------------------------------------------------------------------------
;; DOM Helpers

(defn get-body []
  (aget (get-elements-by-tag-name-and-class "body") 0))


;; -----------------------------------------------------------------------------

(defn set-html! [el s]
  (set! (.-innerHTML el) s))

(defn set-text! [el s]
  (dom/setTextContent el s))

(defn set-class! [el name]
  (classes/set el name))

(defn add-class! [el name]
  (classes/add el name))

(defn remove-class! [el name]
  (classes/remove el name))

(defn tag-match [tag]
  (fn [el]
    (when-let [tag-name (.-tagName el)]
      (= tag (.toLowerCase tag-name)))))

(defn parent [el tag]
  (let [matcher (tag-match tag)]
    (if (matcher el)
      el
      (dom/getAncestor el (tag-match tag)))))

(defn el-matcher [el]
  (fn [other] (identical? other el)))

(defn by-tag-name [el tag]
  (prim-seq (dom/getElementsByTagNameAndClass tag nil el)))

(defn offset [el]
  [(style/getPageOffsetLeft el) (style/getPageOffsetTop el)])

(defn in? [e el]
  (let [target (.-target e)]
    (or (identical? target el)
        (not (nil? (dom/getAncestor target (el-matcher el)))))))


(def keyword->event-type
  {:keyup goog.events.EventType.KEYUP
   :keydown goog.events.EventType.KEYDOWN
   :keypress goog.events.EventType.KEYPRESS
   :click goog.events.EventType.CLICK
   :dblclick goog.events.EventType.DBLCLICK
   :mousedown goog.events.EventType.MOUSEDOWN
   :mouseup goog.events.EventType.MOUSEUP
   :mouseover goog.events.EventType.MOUSEOVER
   :mouseout goog.events.EventType.MOUSEOUT
   :mousemove goog.events.EventType.MOUSEMOVE
   :focus goog.events.EventType.FOCUS
   :blur goog.events.EventType.BLUR})


(def ENTER 13)
(def UP_ARROW 38)
(def DOWN_ARROW 40)
(def TAB 9)
(def ESC 27)

(def KEYS #{UP_ARROW DOWN_ARROW ENTER TAB ESC})

(defn key-event->keycode [e]
  (.-keyCode e))

(defn key->keyword [code]
  (condp = code
    UP_ARROW   :previous
    DOWN_ARROW :next
    ENTER      :select
    TAB        :select
    ESC        :exit))

(def request-animation-frame
  (or
   (.-requestAnimationFrame js/window)
   (.-webkitRequestAnimationFrame js/window)
   (.-mozRequestAnimationFrame js/window)
   (.-msRequestAnimationFrame js/window)
   (.-oRequestAnimationFrame js/window)
   (let [t0 (.getTime (js/Date.))]
     (fn [f]
       (js/setTimeout
        #(f (- (.getTime (js/Date.)) t0))
        16.66666)))))

;; From hiccup.compiler:
(def ^{:doc "Regular expression that parses a CSS-style id and class from an element name."
       :private true}
  re-tag #"([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?")

(def ^:private re-dot (js/RegExp. "\\." "g"))

(defn create-or-find-node! [id] ; or find-or-create-node!
  (if-let [node (find-by-id id)]
    node
    (let [node (.createElement js/document "div")]
      (set! (.-id node) id)
      (.appendChild (.-body js/document) node))))

(defn now [] (js/Date.))

(defn error? [x]
  (instance? js/Error x))

(defn throw-err [x]
  (if (error? x)
    (throw x)
    x))
