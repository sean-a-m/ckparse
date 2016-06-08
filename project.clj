(defproject ck2parse "0.1.0-SNAPSHOT"
  :description "Parse"
  :url "N/A"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [instaparse "1.4.2"]]
  :main ck2parse.core
  :aot [ck2parse.core ck2parse.parser]
  :target-path "target/%s"
  :jvm-opts ["-Xms4g"]
  ;           "-Dcom.sun.management.jmxremote"
  ;           "-Dcom.sun.management.jmxremote.ssl=false"
  ;           "-Dcom.sun.management.jmxremote.authenticate=false"
  ;           "-Dcom.sun.management.jmxremote.port=43212"]
  :profiles {:uberjar {:aot :all}})
