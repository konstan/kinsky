(defproject org.clojars.konstan/kinsky-test-jar "version-is-inherited"

            :description "Test jar for org.clojars.konstan/kinsky"

            :plugins [[lein-parent "0.3.5"]]

            :parent-project {:path "../project.clj"
                             :inherit [:version
                                       :license
                                       :url
                                       :plugins
                                       :min-lein-version
                                       :managed-dependencies
                                       :repositories
                                       :deploy-repositories
                                       :resource-paths]}
            :source-paths ["../test"]
            :dependencies [[org.apache.kafka/kafka_2.13 "3.1.0"]]
            :clean-targets ^{:protect false} ["target"])
