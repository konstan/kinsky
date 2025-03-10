**NB!**

The Kafka client in this repository is fully based
on [https://github.com/pyr/kinsky](https://github.com/pyr/kinsky), and this is
where all the credits should go.

The following updates to [https://github.com/pyr/kinsky](https://github.com/pyr/kinsky)
library and repository were made:

* minor updates to be able to use Kafka 3.x Java library,
* embedded test server uses Kafka 3.x in KRaft mode,
* added test jar.

The library and tests jar are available on Clojars. Example usage with Leiningen:

```clojure
:dependencies
[[org.clojars.konstan/kinsky "0.3.0"]]

:profiles
{
 :test {:dependencies [[org.clojars.konstan/kinsky-test-jar "0.3.0"]]}
 }
```

Kinsky: Clojure Kafka client library
====================================

[![Build Status](https://secure.travis-ci.org/pyr/kinsky.svg)](http://travis-ci.org/pyr/kinsky)

Kinsky is a *somewhat* opinionated client library
for [Apache Kafka](https://kafka.apache.org) in Clojure.

Kinsky provides the following:

- Kafka 2.4.x compatibility
- Adequate data representation of Kafka types.
- Default serializer and deserializer implementations such as
  **JSON**, **EDN** and a **keyword** serializer for keys.
- Documentation

## Usage

```clojure
   [[spootnik/kinsky "0.1.23"]]
```

## Documentation

* [API Documentation](https://pyr.github.io/kinsky)

## Contributors

Thanks a lot to these awesome contributors

- Ray Cheung (@raycheung)
- Daniel Truemper (@truemped)
- Mathieu Marchandise (@vielmath)
- Eli Sorey (@esorey)
- François Rey (@fmjrey)
- Karthikeyan Chinnakonda (@codingkarthik)
- Jean-Baptiste Besselat (@luhhujbb)
- Carl Düvel (@hackbert)
- Andrew Garman (@agarman)
- Pyry Kovanen (@pkova)
- Henrik Lundahl (@henriklundahl)
- Josh Glover (@jmglov)
- Marcus Spiegel (@malesch)
- Jeff Stokes (@jstokes)
- Martino (@vise890-ovo)
- Ikuru K (@iku000888)

## Changelog

### 0.1.26

- Bump kafka dependency to v2.7.x

### 0.1.25

- Removal of the asynchronous façade, transducers should suffice

### 0.1.24

- Add support for producer transaction

### 0.1.23

- Fixed ConcurrentModificationException when async consumer created with topic
- Added support for reader opts when consuming
- Add timestamp in record output

### 0.1.22

- Update to latest Kafka clients
- Typo fix

### 0.1.21

- Update to latest Kafka clients
- Provide duplex channels to bridge control and record channels in consumers

### 0.1.16

- Stability and bugfix release
- Lots of input from @jmgrov, @scott-abernethy, and @henriklundahl. Thanks!

## Examples

The examples assume the following require forms:

```clojure
(:require [kinsky.client :as client])
```

### Production

```clojure
(let [p (client/producer {:bootstrap.servers "localhost:9092"}
                         (client/keyword-serializer)
                         (client/edn-serializer))]
  (client/send! p "account" :account-a {:action :login}))

```

### Consumption

```clojure
(let [c (client/consumer {:bootstrap.servers "localhost:9092"
                          :group.id          "mygroup"}
                         (client/keyword-deserializer)
                         (client/edn-deserializer))]
  (client/subscribe! c "account")
  (client/poll! c 100))

```
