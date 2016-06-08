(ns ck2parse.core
   (:gen-class)
   (require [clojure.java.io :as io]
            [instaparse.core :as ip]
            [ck2parse.parser :as parser]))

(defn -main
  [& args]
  (let [ckgrammar (ip/parser "resources/grammar")
        output-file "test/dictoutput.txt"]
    (println "Running...")
    (with-open [rdr (io/reader "test/sample5.ck2")]
      (spit output-file
        (parser/pC (line-seq rdr) ckgrammar {})))))


