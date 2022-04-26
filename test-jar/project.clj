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
                                       :resource-paths
                                       :pom-location
                                       [:profiles :provided]]}
            :source-paths ["../test"]
            :clean-targets ^{:protect false} ["target"]
            )
