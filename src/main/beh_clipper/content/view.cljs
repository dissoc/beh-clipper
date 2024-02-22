(ns beh-clipper.content.view
  (:require
   [beh-clipper.content.events :as events]
   [re-frame.core :refer [dispatch]]
   [taoensso.timbre :refer [info]]))

(defn init []
  (info "Initializing clipper extension...")
  (dispatch [::events/init-content-script])
  (info "Initialization complete"))
