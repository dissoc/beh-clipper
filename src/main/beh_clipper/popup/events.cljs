(ns beh-clipper.popup.events
  (:require [cognitect.transit :as t]
            [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [taoensso.timbre :refer [info debug]]))

(def writer (t/writer :json))
(def reader (t/reader :json))

(reg-event-fx
 ::init-popup-script
 []
 (fn [{:keys [db]} [_]]
   {::start-message-listener nil}))
