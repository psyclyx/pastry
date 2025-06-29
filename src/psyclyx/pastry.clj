(ns psyclyx.pastry
  (:require
    [donut.system :as-alias donut]
    [donut.system.plugin :as-alias plugin]
    [malli.core :as m]))


(defmulti ->component ::type :default ::default)


(defn -update-components
  "Updates all component-level values in `system` for which `pred` is true with `f`."
  [system pred f & args]
  (update system ::donut/defs update-vals
          (fn [?components]
            (update-vals ?components #(if (pred %)
                                        (apply f % args)
                                        %)))))


(defn -merge-components
  "Updates all component-level values in `system` as in `-update-components`, using a predicate and
  updating function retrieved from the system.

  Also see `pastry-plugin`."
  [{::keys [->component target?] :as system}]
  (-update-components system target? ->component))


(def PastryTarget [:map [::type :keyword]])
(def -pastry-target? (m/validator PastryTarget))


(def pastry-plugin
  {::plugin/name ::component-plugin
   ::plugin/doc "A donut.system plugin that transforms component-level targets into donut.system components.

Requires two top-level keys:
- `::->component`: Function taking a target and returning a donut.system
  component map.
- `::target?`: Predicate identifying component-level values to be transformed
    with `::->component` (defaults to maps with `:psyclyx.pastry/type`). "
   ::plugin/system-defaults {::->component ->component
                             ::target? -pastry-target?}
   ::plugin/system-update -merge-components})
