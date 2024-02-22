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
  (let [click-delay (subscribe [::subs/click-delay])
        click-delay-view (subscribe [::subs/click-delay-view])]
    (fn []
      [rc/v-box
       :gap "10px"
       :align :center
       :children [[rc/label
                   :style (:label style)
                   :label "click delay (ms)"]
                  [rc/label
                   :style (:label style)
                   :label (str @click-delay)]
                  [rc/slider
                   :model click-delay
                   :width "160px"
                   :min 1000
                   :step 500
                   :max 8000
                   :on-change #(dispatch [::events/set-click-delay %])]]])))

(defn simulate-config []
  (let [simulate? (subscribe [::subs/simulate?])]
    (fn []
      [rc/h-box
       :children [[rc/checkbox
                   :style (:checkbox style)
                   :model simulate?
                   :on-change #(dispatch [::events/toggle-simulate])]
                  [rc/gap :size "10px"]
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

(defn clip-buttons []
  (let [click-delay (subscribe [::subs/click-delay])
        simulate?   (subscribe [::subs/simulate?])]
    (fn []
      [rc/v-box
       :gap "10px"
       :align :center
       :children
       [[rc/button
         :style (:button style)
         :label "Clip page coupons"
         :on-click #(dispatch [::events/clip-coupons {:click-delay @click-delay
                                                      :keep-going? false
                                                      :simulate    @simulate?}])]
        [rc/button
         :style (:button style)
         :label "Clip all coupons"
         :on-click #(dispatch [::events/clip-coupons {:click-delay @click-delay
                                                      :keep-going? true
                                                      :simulate    @simulate?}])]]])))

(defn page []
  [rc/v-box
   :gap "10px"
   :align :center
   :style (merge {:padding "10px"}
                 (select-keys style [:background-color]))

   :children [[rc/label
               :style (:label style)
               :label "BEH Clipper"]
              [:img {:src    "./icons/scissors-svgrepo-com-128x128.png"
                     :width  124
                     :height 124}]
              [clip-buttons]
              [config]]])

(defn mount-components []
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init []
  (info "Initializing clipper popup")
  (dispatch [::events/init-popup-script])
  (mount-components))
