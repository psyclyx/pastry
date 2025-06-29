(ns psyclyx.pastry.readme-test
  (:require
    [clojure.test :refer [deftest is]]
    [donut.system :as donut]
    [psyclyx.pastry :as pastry]))


(defmethod pastry/->component :my-app/greeter
  [{:keys [greeting]}]
  {::donut/start (constantly greeting)})


(deftest default-usage
  (let [system {::donut/plugins [pastry/pastry-plugin]
                ::donut/defs
                {:app {:greeter {::pastry/type :my-app/greeter
                                 :greeting "Hello, pastry!"}}}}]
    (is (= "Hello, pastry!"
           (-> (donut/signal system ::donut/start)
               (get-in [::donut/instances :app :greeter]))))))


(deftest custom-usage
  (let [system {::donut/plugins [pastry/pastry-plugin]
                ::pastry/target? string?
                ::pastry/->component (fn [s] {::donut/start (constantly (count s))})
                ::donut/defs
                {:app {:string-length "a string definition"}}}]
    (is (= 19
           (-> (donut/signal system ::donut/start)
               (get-in [::donut/instances :app :string-length]))))))
