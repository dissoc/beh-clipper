(ns beh-clipper.content.events
  (:require [cognitect.transit :as t]
            [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [taoensso.timbre :refer [info debug]]))

(reg-event-fx
 ::init-content-script
 []
 (fn [{:keys [db]} [_]]
   {::start-event-listener nil}))
