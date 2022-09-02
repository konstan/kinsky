(ns kinsky.embedded-kraft
  "Based on crux.kafka.embedded"
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s])
  (:import (java.io Closeable)
           (java.nio.file Files FileVisitResult Path Paths SimpleFileVisitor)
           (org.apache.kafka.common
             Uuid)
           (kafka.tools
             StorageTool)
           (kafka.server
             KafkaConfig
             KafkaRaftServer)
           (scala Option)
           (org.apache.kafka.common.utils
             Time)))

(def default-kafka-server-config
  {"process.roles"                            "broker,controller"
   "node.id"                                  "1"
   "controller.quorum.voters"                 "1@localhost:9093"
   "listeners"                                "PLAINTEXT://localhost:9092,CONTROLLER://localhost:9093"
   "inter.broker.listener.name"               "PLAINTEXT"
   "advertised.listeners"                     "PLAINTEXT://localhost:9092"
   "controller.listener.names"                "CONTROLLER"
   "listener.security.protocol.map"           "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL"
   "num.network.threads"                      "3"
   "num.io.threads"                           "8"
   "socket.send.buffer.bytes"                 "102400"
   "socket.receive.buffer.bytes"              "102400"
   "socket.request.max.bytes"                 "104857600"
   "num.partitions"                           "1"
   "num.recovery.threads.per.data.dir"        "1"
   "offsets.topic.replication.factor"         "1"
   "transaction.state.log.replication.factor" "1"
   "transaction.state.log.min.isr"            "1"
   "log.retention.hours"                      "168"
   "log.segment.bytes"                        "1073741824"
   "log.retention.check.interval.ms"          "300000"
   "log.dirs"                                 "/tmp/kraft-combined-logs"
   })


(defn start-kafka-broker ^KafkaRaftServer
  [config]
  (let [conf-final (merge default-kafka-server-config config)
        kafka-config (KafkaConfig. conf-final true)
        cluster-id (.toString (Uuid/randomUuid))]
    (StorageTool/formatCommand
      System/out
      (StorageTool/configToLogDirectories kafka-config)
      (StorageTool/buildMetadataProperties cluster-id kafka-config)
      false)
    (doto (KafkaRaftServer.
            kafka-config
            Time/SYSTEM
            (Option/apply (str "TestBroker:" (get conf-final "broker.id"))))
      (.startup))))

(defn stop-kafka-broker [^KafkaRaftServer broker]
  (some-> broker .shutdown)
  (some-> broker .awaitShutdown))

(def file-deletion-visitor
  (proxy [SimpleFileVisitor] []
    (visitFile [file _]
      (Files/delete file)
      FileVisitResult/CONTINUE)

    (postVisitDirectory [dir _]
      (Files/delete dir)
      FileVisitResult/CONTINUE)))

(defn create-tmp-dir
  ^Path [prefix]
  (Files/createTempDirectory
    (Paths/get (System/getProperty "java.io.tmpdir")
               (make-array String 0))
    (str prefix "-")
    (make-array java.nio.file.attribute.FileAttribute 0)))

(defn delete-dir
  [dir]
  (assert (instance? Path dir))
  (Files/walkFileTree dir
                      file-deletion-visitor))

(defrecord EmbeddedKafka [kafka options]
  Closeable
  (close [_]
    (stop-kafka-broker kafka)))

(s/def ::host string?)
(s/def ::port pos-int?)
(s/def ::log-dirs string?)
(s/def ::server-config (s/map-of string? string?))

(s/def ::options (s/keys :req [::log-dirs]
                         :opt [::host
                               ::port
                               ::server-config]))

(defn start-embedded-kafka
  "Starts Kafka in KRaft mode locally. This can be used to run server in
  a self-contained single node mode. The option log-dir is required.

  Returns a EmbeddedKafka component that implements java.io.Closeable,
  which allows Kafka to be stopped by calling close."
  ^java.io.Closeable
  [{::keys [host
            port
            log-dirs
            server-config]
    :or    {host  "localhost"
            port  9092}
    :as    options}]
  (s/assert ::options options)
  (let [plaintext (format "%s:%d" host port)
        controller (format "%s:%d" host (+ port 1))
        kafka (start-kafka-broker (assoc server-config
                                    "log.dirs" (str (io/file log-dirs))
                                    "controller.quorum.voters" (format "1@%s" controller)
                                    "listeners" (format "PLAINTEXT://%s,CONTROLLER://%s" plaintext controller)
                                    "advertised.listeners" (format "PLAINTEXT://%s" plaintext)))]
    (->EmbeddedKafka kafka
                     (assoc options
                       :bootstrap-servers plaintext))))

