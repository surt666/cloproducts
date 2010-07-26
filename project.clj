(defproject cloproducts "1.0.0-SNAPSHOT"
  :description "Clojure produkt og abonnement projekt"
  :dependencies
    [[org.clojure/clojure "1.1.0"]
     [org.clojure/clojure-contrib "1.1.0"]
     [compojure "0.4.1"]
     [hiccup "0.2.6"]
     [ring/ring-core "0.2.5"]
     [ring/ring-servlet "0.2.5"]
     [ring/ring-jetty-adapter "0.2.5"]
     [org.clojars.the-kenny/clojure-couchdb "0.2.1"]
     [clj-time "0.1.0-SNAPSHOT"]
     [enlive "1.0.0-SNAPSHOT"]
     [sandbar/sandbar-core "0.3.0"]
     [sandbar/sandbar-session "0.2.4"]
     [joda-time "1.6"]]
  :dev-dependencies
    [[ring/ring-devel "0.2.5"]
     [swank-clojure "1.1.0"]
     [leiningen/lein-swank "1.1.0"]
     [uk.org.alienscience/leiningen-war "0.0.3"]]
  :namespaces [myapp.servlet])
