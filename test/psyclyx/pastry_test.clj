(ns psyclyx.pastry-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [donut.system :as donut]
    [donut.system.plugin :as dsp]
    [psyclyx.pastry :as pastry]))


(defmethod pastry/->component :test/component
  [{:keys [value]}]
  {::donut/start (constantly value)})


(deftest definition-transformation-test
  (testing "Plugin correctly transforms system definitions"
    (let [regular (constantly "regular")
          system {::donut/plugins [pastry/pastry-plugin]
                  ::donut/defs
                  {:app {:pastry-component {::pastry/type :test/component
                                            :value 42}
                         :regular-component {::donut/start regular}}}}
          transformed (dsp/apply-plugins system)]
      (is (contains? (get-in transformed [::donut/defs :app :pastry-component]) ::donut/start)
          "Transforms target by adding a ::donut/start key")
      (is (= {::donut/start regular}
             (get-in transformed [::donut/defs :app :regular-component]))
          "Does not transform a regular component definition"))))


(deftest system-lifecycle-test
  (testing "Starts a system with default pastry settings"
    (let [system {::donut/plugins [pastry/pastry-plugin]
                  ::donut/defs
                  {:app {:value-producer {::pastry/type :test/component
                                          :value 42}
                         :value-consumer {::donut/start (fn [{{:keys [val]} ::donut/config}]
                                                          (inc val))
                                          ::donut/config {:val (donut/ref [:app :value-producer])}}}}}
          started (donut/signal system ::donut/start)]
      (is (not (::donut/error started)) "System starts without errors")
      (is (= 42 (get-in started [::donut/instances :app :value-producer]))
          "Pastry component instance is correct")
      (is (= 43 (get-in started [::donut/instances :app :value-consumer]))
          "Dependent component instance is correct")))

  (testing "Starts a system with custom pastry settings"
    (let [system {::donut/plugins [pastry/pastry-plugin]
                  ::pastry/target? string?
                  ::pastry/->component (fn [s] {::donut/start (constantly (count s))})
                  ::donut/defs
                  {:app {:string-producer "a string target"}}}
          started (donut/signal system ::donut/start)]
      (is (not (::donut/error started)) "System starts without errors")
      (is (= 15 (get-in started [::donut/instances :app :string-producer]))
          "Custom pastry component instance is correct"))))
