;;; Copyright © 2024 Justin Bishop (concat "mail" @ "dissoc.me")

(ns beh-clipper.content.events
  (:require
   [cognitect.transit :as t]
   [re-frame.core :refer [dispatch reg-event-fx reg-fx]]
   [taoensso.timbre :refer [info]]))

(def writer (t/writer :json))
(def reader (t/reader :json))

(reg-event-fx
 ::init-content-script
 []
 (fn [{:keys [db]} [_]]
   {::start-event-listener nil}))

(reg-fx
 ::start-event-listener
 (fn [msg]
   (info "Creating message listener")
   (js/chrome.runtime.onMessage.addListener
    (fn [transit-event sender send-response]
      (let [event (t/read reader transit-event)]
        (info "received event")
        (dispatch event))))))

(defn get-clippable-btns
  "returns the buttons that are clippable aka should be clicked.
  Should omit buttons that have already been clipped"
  []
  (->> "button"
       (. js/document getElementsByTagName)
       (filter (fn [el]
                 (and (try (not (.-disabled el))
                           (catch js/Error e
                             false))
                      ;; also child has clip text
                      ;; NOTE: this could change
                      (let [child (.-children el)]
                        (when-let [span-child (aget child 0)]
                          (let [node-name (.-nodeName  span-child)
                                span-text (.-textContent span-child)]
                            (and (= "SPAN" node-name)
                                 (= "Clip" span-text))))))))))

(defn get-view-more-btn []

  ;; old version with "view more" button
  ;; (->> "button"
  ;;      (. js/document getElementsByTagName)
  ;;      (filter (fn [el]
  ;;                (and (try (not (.-disabled el))
  ;;                          (catch js/Error e
  ;;                            false))
  ;;                     ;; also child has clip
  ;;                     (let [child (.-children el)]
  ;;                       (when-let [span-child (aget child 0)]
  ;;                         (let [node-name (.-nodeName  span-child)
  ;;                               span-text (.-textContent span-child)]
  ;;                           (and (= "SPAN" node-name)
  ;;                                (= "View more" span-text))))))))
  ;;      first)

  ;; console selector
  ;; document.querySelectorAll('[data-qe-id="paginationNext"]')[0].click();
  (->> "button"
       (. js/document querySelectorAll "[data-qe-id=\"paginationNext\"]")
       first))

(reg-fx
 ::click-button
 (fn [{button      :button
       button-type :button-type
       on-click    :on-click
       conf        :conf}]
   (info "simulating clicking button")
   (let [{click-delay :click-delay
          simulate?   :simulate?} conf]
     (let [click-delay (if (string? click-delay)
                         (js/parseInt click-delay)
                         click-delay)]
       (. button scrollIntoView)
       (if simulate?
         (when (= :view-more button-type)
           (. button click))
         (. button click))
       (js/setTimeout on-click click-delay)))))

(reg-event-fx
 ::click-buttons-handler
 []
 (fn [{:keys [db]} [_ {coupon-btns :coupon-btns
                       more-btn    :more-btn
                       conf        :conf
                       :as         m}]]
   (let [{click-delay :click-delay
          keep-going? :keep-going?
          simulate?   :simulate?} conf]
     (if (not (empty? coupon-btns))
       {::click-button
        {:button   (first coupon-btns)
         :conf     conf
         :on-click #(dispatch [::click-buttons-handler
                               (assoc m :coupon-btns (rest coupon-btns))])}}
       (when keep-going?
         (let [more-btn (get-view-more-btn)]
           (when more-btn
             {::click-button
              {:button      more-btn
               :button-type :view-more
               :conf        conf
               :on-click    #(dispatch [:start-clipping-process conf])}})))))))

(reg-fx
 ::get-page-buttons
 (fn [conf]
   (let [view-more-button (get-view-more-btn)
         buttons          (get-clippable-btns)]
     (info "getting page buttons")
     (dispatch [::click-buttons-handler {:conf        conf
                                         :coupon-btns buttons
                                         :more-btn    view-more-button}]))))

(reg-event-fx
 :start-clipping-process
 []
 (fn [{:keys [db]} [_ conf]]
   (info "starting clipping process")
   {::get-page-buttons conf
    :db                db}))
