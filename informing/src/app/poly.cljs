(ns app.poly
  (:require
   [cljs.core :as cljs]
   [cljs.core.async :refer [<! chan put! sliding-buffer]]
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
           [goog.events.EventType]))


;; -----------------------------------------------------------------------------
;; DOM Utilities

(defn get-viewport-size []
  (dom/getViewportSize))

(defn get-viewport-width []
  (.-width (dom/getViewportSize)))

(defn get-viewport-height []
  (.-height (dom/getViewportSize)))

(defn get-document-height []
  (dom/getDocumentHeight))

(defn get-document-scroll-x []
  (.-x (dom/getDocumentScroll)))

(defn get-document-scroll-y []
  (.-y (dom/getDocumentScroll)))

(defn get-root []
  (aget (dom/getElementsByTagNameAndClass "html") 0))

(defn get-body []
  (aget (dom/getElementsByTagNameAndClass "body") 0))

(defn get-document []
  (dom/getDocument))

(defn get-element [id]
  (dom/getElement (name id)))

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

(defn set-stylesheet! [stylesheet]
  (let [el (.createElement js/document "style")
        node (.createTextNode js/document stylesheet)]
    (.appendChild el node)
    (.appendChild (.-head js/document) el)))

(defn set-title! [title]
  (set! (.-title js/document) title))


;; -----------------------------------------------------------------------------
;; Misc

(defn atom? [x]
  (instance? Atom x))

(defn now [] (js/Date.))



;; -----------------------------------------------------------------------------
;; Stuff that might be useful but needs to be vetted.

;; (defn get-elements-by-tag-name-and-class
;;   ([tag-name]
;;    (dom/getElementsByTagNameAndClass (name tag-name)))
;;   ([tag-name class-name]
;;    (dom/getElementsByTagNameAndClass (name tag-name) (name class-name))))

;; (defn by-tag-name
;;   ([tag-name] (by-tag-name tag-name nil))
;;   ([tag-name el] (array-seq (dom/getElementsByTagNameAndClass tag-name nil el))))

;; (defn by-tag-name
;;   [el tag]
;;   (prim-seq (dom/getElementsByTagNameAndClass tag nil el)))

;; (defn- get-scroll []
;;   (-> (dom/getDocumentScroll) (.-y)))

;; (defn query
;;   ([s]
;;    (dom/query (name s)))
;;   ([base s]
;;    (dom/query (name s) base)))


;; -----------------------------------------------------------------------------
;; DOM Synonyms

;; (def body get-body)

;; (def by-id get-element)

;; (def find-by-id get-element)

;; (def get-element-by-id get-element)

;; (def root get-root)


;; -----------------------------------------------------------------------------

;; (defn by-id "" [id] (gdom/getElement id))
;; (defn by-class "" [class & [root]] (gdom/getElementsByClass class root))
;; (defn by-class1 "" [class & [root]] (gdom/getElementByClass class root))
;; (defn by-tag-and-class "" [tag class & [root]] (gdom/getElementsByTagNameAndClass tag class root))
;; (defn by-tag-and-class1 "" [tag class & [root]] (first (by-tag-and-class tag class root)))
;; (defn by-tag "" [tag & [root]] (gdom/getElementsByTagNameAndClass tag nil root))
;; (defn by-tag1 "" [tag & [root]] (first (by-tag tag root)))

;; (defn append! "" [node & xs] (apply gdom/append node xs) node)
;; (defn prepend! "" [node & xs] (doseq [x (reverse xs)] (gdom/insertChildAt node x 0)) node)
;; (defn insert-before! "" [node ref] (gdom/insertSiblingBefore node ref) node)
;; (defn insert-after! "" [node ref] (gdom/insertSiblingAfter node ref) node)

;; (defn text-node "" [txt] (gdom/createTextNode txt))

;; (defn replace! "" [old new] (gdom/replaceNode new old))

;; (defn remove! "" [node] (gdom/removeNode node))
;; (defn remove-children! "" [node] (gdom/removeChildren node))

;; (defn last-elem-child "" [x] (gdom/getLastElementChild x))

;; (defn set-html! [el s]
;;   (set! (.-innerHTML el) s))

;; (defn set-text! [el s]
;;   (dom/setTextContent el s))

;; (defn set-class! [el name]
;;   (classes/set el name))

;; (defn add-class! [el name]
;;   (classes/add el name))

;; (defn remove-class! [el name]
;;   (classes/remove el name))

;; (defn set-interval! [f delta]
;;   (js/setInterval f delta))

;; (defn set-timeout! [f delta]
;;   (js/setTimeout f delta))

;; (defn clear-timeout! [timeout]
;;   (js/clearTimeout timeout))

;; (defn tag-match [tag]
;;   (fn [el]
;;     (when-let [tag-name (.-tagName el)]
;;       (= tag (.toLowerCase tag-name)))))

;; (defn parent [el tag]
;;   (let [matcher (tag-match tag)]
;;     (if (matcher el)
;;       el
;;       (dom/getAncestor el (tag-match tag)))))

;; (defn el-matcher [el]
;;   (fn [other] (identical? other el)))

;; (defn offset [el]
;;   [(style/getPageOffsetLeft el) (style/getPageOffsetTop el)])

;; (defn in? [e el]
;;   (let [target (.-target e)]
;;     (or (identical? target el)
;;         (not (nil? (dom/getAncestor target (el-matcher el)))))))


;; (def keyword->event-type
;;   {:keyup goog.events.EventType.KEYUP
;;    :keydown goog.events.EventType.KEYDOWN
;;    :keypress goog.events.EventType.KEYPRESS
;;    :click goog.events.EventType.CLICK
;;    :dblclick goog.events.EventType.DBLCLICK
;;    :mousedown goog.events.EventType.MOUSEDOWN
;;    :mouseup goog.events.EventType.MOUSEUP
;;    :mouseover goog.events.EventType.MOUSEOVER
;;    :mouseout goog.events.EventType.MOUSEOUT
;;    :mousemove goog.events.EventType.MOUSEMOVE
;;    :focus goog.events.EventType.FOCUS
;;    :blur goog.events.EventType.BLUR
;;    :scroll goog.events.EventType.SCROLL
;;    :resize goog.events.EventType.RESIZE
;;    :navigate goog.history.EventType.NAVIGATE})

;; (defn listen
;;   ([el type] (listen el type nil))
;;   ([el type f] (listen el type f (chan (sliding-buffer 1))))
;;   ([el type f out]
;;    (events/listen el (keyword->event-type type)
;;                   (fn [e] (when f (f e)) (put! out e)))
;;    out))

;; (def ENTER 13)
;; (def UP_ARROW 38)
;; (def DOWN_ARROW 40)
;; (def TAB 9)
;; (def ESC 27)

;; (def KEYS #{UP_ARROW DOWN_ARROW ENTER TAB ESC})

;; (defn key-event->keycode [e]
;;   (.-keyCode e))

;; (defn key->keyword [code]
;;   (condp = code
;;     UP_ARROW   :previous
;;     DOWN_ARROW :next
;;     ENTER      :select
;;     TAB        :select
;;     ESC        :exit))

;; From hiccup.compiler:
;; (def ^{:doc "Regular expression that parses a CSS-style id and class from an element name."
;;        :private true}
;;   re-tag #"([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?")

;; (def ^:private re-dot (js/RegExp. "\\." "g"))

;; (defn create-or-find-node! [id] ; or find-or-create-node!
;;   (if-let [node (find-by-id id)]
;;     node
;;     (let [node (.createElement js/document "div")]
;;       (set! (.-id node) id)
;;       (.appendChild (.-body js/document) node))))

;; (defn error? [x]
;;   (instance? js/Error x))

;; (defn throw-err [x]
;;   (if (error? x)
;;     (throw x)
;;     x))
