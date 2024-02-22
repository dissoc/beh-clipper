;;; Copyright © 2024 Justin Bishop (concat "mail" @ "dissoc.me")

(ns beh-clipper.popup.view
  (:require
   [beh-clipper.popup.events :as events]
   [beh-clipper.popup.subs :as subs]
   [beh-clipper.style :refer [style]]
   [re-com.core :as rc]
   [re-frame.core :refer [dispatch subscribe]]
   [reagent.dom :as rdom]
   [taoensso.timbre :refer [info]]))

(defn click-delay-config []
  (let [click-delay (subscribe [::subs/click-delay])]
    (fn []
      [rc/v-box
       :gap "10px"
       :align :center
       :children
       [[rc/label
         :style (:label style)
         :label "click delay (ms)"]
        [rc/input-text
         :width "60px"
         :model click-delay
         :on-change #(dispatch [::events/set-click-delay %])]]])))

(defn simulate-config []
  (let [simulate? (subscribe [::subs/simulate?])]
    (fn []
      [rc/h-box
       :children
       [[rc/checkbox
         :style (:checkbox style)
         :model simulate?
         :on-change #(dispatch [::events/toggle-simulate])]
        [rc/gap :size "6px"]
        [rc/label
         :style (:label style)
         :label "simulate"]]])))

(defn config []
  (fn []
    [rc/v-box
     :align :center
     :gap "10px"
     :children
     [[click-delay-config]
      [simulate-config]]]))

(defn page []
  [rc/v-box
   :gap "10px"
   :align :center
   :style (merge {:padding "10px"}
                 (select-keys style [:background-color]))

   :children [[rc/label
               :style (:color style)
               :label "BEH Clipper"]
              [:img {:src    "./icons/scissors-svgrepo-com-128x128.png"
                     :width  124
                     :height 124}]
              [rc/button
               :style (:button style)
               :label "Clip page coupons"
               :on-click #(dispatch [::events/clip-page-coupons])]
              [rc/button
               :style (:button style)
               :label "Clip all coupons"
               :on-click #(dispatch [::events/clip-all-coupons])]
              [config]]])

(defn mount-components []
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init []
  (info "Initializing clipper popup")
  (dispatch [::events/init-popup-script])
  (mount-components))
