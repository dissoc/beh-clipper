(ns beh-clipper.content.view
  (:require [re-frame.core :refer [dispatch]]
            [taoensso.timbre :refer [info debug]]))
(defn init []
  (info "Initializing clipper extension...")
  ;;(devtools/install!)
  (dispatch [::events/init-content-script])
  (info "Initialization complete"))
