(ns beh-clipper.popup.view
  (:require [re-frame.core :refer [dispatch subscribe]]
            [taoensso.timbre :refer [info debug]]))

(defn init []
  (info "Initializing clipper popup")
  (dispatch [::events/init-popup-script]))
