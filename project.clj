(defproject muuuuu "0.1.0-SNAPSHOT"
  :description "working title - p2p music filesharing in the webbrowser!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.2"]
                 [secretary "1.0.2"]
                 [sablono "0.2.6"]
                 [com.facebook/react "0.9.0"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :output-to "out/muuuuu.js"
                :output-dir "out"
                :optimizations :none
                :source-map true }
              :notify-command ["growlnotify" "-n" "ClojureScript compiler says:" "-m"]}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :output-to "dist/muuuuu.js"
                :output-dir "dist"
                :optimizations :advanced
                :preamble ["resources/js-ext/jquery.min.js"]
                :externs ["resources/js-ext/jquery.customEvents.js"
                          "resources/js-ext/jquery.panelSnap.js"]
                :closure-warnings {:externs-validation :off
                                   :non-standard-jsdoc :off}}}]
    :test-commands {"unit" ["phantomjs" "test/tests.js"]}})
