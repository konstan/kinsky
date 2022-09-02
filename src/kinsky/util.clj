(ns kinsky.util
  (:import [java.util Map]))

(defn opts->props
  "Kakfa configs are now maps of strings to strings. Morphs an arbitrary
  clojure map into this representation.  Make sure we don't pass
  options that are meant for the driver to concrete Consumers/Producers"
  ^Map [opts]
  (into {}
        (comp
         (filter (fn [[k _]] (not (qualified-keyword? k))))
         (map (fn [[k v]] [(name k) (str v)])))
        opts))
