(defproject org.clojars.konstan/kinsky "0.3.0"
  :description "Kafka clojure client library with tests against Kafka 3.x in KRaft mode. Fully based on https://github.com/pyr/kinsky"
  :plugins [[lein-ancient "0.6.15"]]
  :url "https://github.com/konstan/kinsky"
  :license {:name "MIT License"
            :url  "https://github.com/konstan/kinsky/tree/master/LICENSE"}
  :global-vars {*warn-on-reflection* true}
  :deploy-repositories [["snapshots" :clojars] ["releases" :clojars]]
  :dependencies [[org.clojure/clojure            "1.10.1"]
                 [org.apache.kafka/kafka-clients "3.1.0"]
                 [metosin/jsonista               "0.2.5"]]
  :test-selectors {:default     (complement :integration)
                   :integration :integration
                   :all         (constantly true)}
  :resource-paths ["test-resources"]
  :profiles {:test {:dependencies [[org.slf4j/slf4j-api "1.7.36"]
                                   [org.slf4j/slf4j-log4j12 "1.7.36"]]}
             :dev {:dependencies [[org.slf4j/slf4j-api "1.7.36"]
                                  [org.slf4j/slf4j-log4j12 "1.7.36"]
                                  ;; for kafka embedded
                                  [org.apache.kafka/kafka_2.13 "3.1.0"]]}})
