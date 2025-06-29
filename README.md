# pastry

A `donut.system` plugin for transforming declarative definitions into components.

## Project status

**Alpha**

This library is a proof of concept. Use at your own risk. Issues and PRs are welcome!

## Installation

**Latest version**: `0.0.1` (tag `v0.0.1`)

### Clojars

#### deps.edn

```clojure
xyz.psyclyx/pastry {:mvn/version "0.0.1"}
```

#### Leiningen/Boot

```clojure
[xyz.psyclyx/pastry "0.0.1"]
```

## Usage

`pastry-plugin` finds and transforms component definitions, allowing you to define components with simple data instead of full `donut.system` maps.

Snippets in this section assume the following requires:

```clojure
(require
  '[donut.system :as donut]
  '[psyclyx.pastry :as pastry])
```

### Default Usage

By default, `pastry` transforms maps containing a `::pastry/type` key. Component creation logic is defined with a `defmethod` for `pastry/->component`.

```clojure
(defmethod pastry/->component :my-app/greeter
  [{:keys [greeting]}]
  {::donut/start (constantly greeting)})

(def system
  {::donut/plugins [pastry/pastry-plugin]
   ::donut/defs
   {:app {:greeter {::pastry/type :my-app/greeter
                    :greeting "Hello, pastry!"}}}})

(-> (donut/signal system ::donut/start)
    (get-in [::donut/instances :app :greeter]))
;; => "Hello, pastry!"
```

### Custom Usage

For more flexibility, the target predicate and transformation function can be configured at the system level.

```clojure
(def system
  {::donut/plugins [pastry/pastry-plugin]
   ::pastry/target? string?
   ::pastry/->component (fn [s] {::donut/start (constantly (count s))})
   ::donut/defs
   {:app {:string-length "a string definition"}}})

(-> (donut/signal system ::donut/start)
    (get-in [::donut/instances :app :string-length]))
;; => 19
```

## Contributing

Issues and pull requests welcome!

## Copyright

Copyright (c) 2025 psyclyx

Released under the MIT license.
