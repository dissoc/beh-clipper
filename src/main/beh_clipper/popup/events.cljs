;;; Copyright © 2024 Justin Bishop (concat "mail" @ "dissoc.me")

(ns beh-clipper.popup.events
  (:require
   [cognitect.transit :as t]
   [re-frame.core :refer [dispatch reg-event-fx reg-fx]]
   [taoensso.timbre :refer [info]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def writer (t/writer :json))
(def reader (t/reader :json))

(reg-event-fx
 ::init-popup-script
 []
 (fn [{:keys [db]} [_]]
   {::start-message-listener nil}))

(reg-event-fx
 ::clip-page-coupons
 []
 (fn [{:keys [db]} [_]]
   {::msg-content [:clip-page-coupons (merge {:keep-going? false}
                                             (:config db))]}))

(reg-event-fx
 ::clip-all-coupons
 []
 (fn [{:keys [db]} [_]]
   {::msg-content [:clip-all-coupons (merge {:keep-going? true}
                                            (:config db))]}))

(reg-event-fx
 ::toggle-simulate
 []
 (fn [{:keys [db]} [_]]
   {:db (update-in db [:config :simulate?] not)}))

(reg-fx
 ::msg-content
 (fn [msg]
   (go
     (info "Sending message to content script")
     (js/chrome.tabs.query #js
                           {:active        true
                            :currentWindow true}
                           (fn [tabs]
                             (let [tab-id      (.-id (aget tabs 0))
                                   msg-transit (t/write writer msg)]
                               (js/chrome.tabs.sendMessage tab-id msg-transit)))))))

(reg-fx
 ::start-message-listener
 (fn [msg]
   (info "Creating message listener")
   (js/chrome.runtime.onMessage.addListener
    (fn [transit-event sender send-response]
      (let [event (t/read reader transit-event)]
        (info "received event")
        (dispatch event))))))
