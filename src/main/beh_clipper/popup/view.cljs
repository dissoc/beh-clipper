(ns beh-clipper.popup.view
  (:require [re-frame.core :refer [dispatch subscribe]]
            [beh-clipper.popup.events :as events]
            [taoensso.timbre :refer [info debug]]))

(defn init []
  (info "Initializing clipper popup")
  (dispatch [::events/init-popup-script]))
