(ns beh-clipper.background
  (:require [shadow.cljs.modern :refer (js-await)]
            [taoensso.timbre :refer [info debug]]))

(defn init []
  (info "Initializing clipper background script")
  (js/chrome.runtime.onMessage.addListener
   (fn [_]
     (info "Received message"))))
