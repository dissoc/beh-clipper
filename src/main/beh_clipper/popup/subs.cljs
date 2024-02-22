;;; Copyright © 2024 Justin Bishop (concat "mail" @ "dissoc.me")

(ns beh-clipper.popup.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::simulate?
 (fn [db _]
   (-> db
       :config
       :simulate?)))
