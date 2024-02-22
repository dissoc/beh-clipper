;;; Copyright © 2024 Justin Bishop (concat "mail" @ "dissoc.me")

(ns beh-clipper.popup.subs
  (:require [re-frame.core :refer [reg-sub]]))

(def default-click-delay "2500")

(reg-sub
 ::simulate?
 (fn [db _]
   (-> db
       :config
       :simulate?)))

(reg-sub
 ::db-click-delay
 ;; the delay that was set by user. if nil then assumed later
 (fn [db _]
   (-> db
       :config
       :click-delay)))

(reg-sub
 ::click-delay
 :<- [::db-click-delay]
 (fn [db-click-delay]
   ;; check if a number and if it is then it can be used
   ;; otherwise default value which seems to work will be used
   (if (or (nil? db-click-delay)
           (js/isNaN db-click-delay))
     default-click-delay
     db-click-delay)))
