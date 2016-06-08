(ns ck2parse.parser
(require [clojure.java.io :as io]
         [clojure.zip :as zip]
         [instaparse.core :as ip]
         [clojure.string :as s]))

(defn map-statement
  "return lval and rval for a string"
  [parsed-array]
  ;(println "parsed array: " parsed-array)
  (let [[entry-type [_ lval] [_ rval]] parsed-array]
    {(keyword lval) rval}))

(defn entry-statement
  [parsed-array]
   (let [[entry-type [lval]] (first parsed-array)]
     (keyword lval)))

(defn nested-keyword
  "Return the keyword from the beginning of a nested statement"
  [parsed-array]
;  (println parsed-array)
  (let [[entry-type [_ lval]] parsed-array]
;    (println (keyword lval))
    (keyword lval)))

(defn array-statement
  "return lval and rval for an array of values on one line"
  [parsed-array]
;  (println parsed-array)
  (let [[entry-type [_ lval] [_ & rvals]] parsed-array]
    {(keyword lval) rvals}))

(defn end-array-statement
  "return rval for an array of values on one line"
  [parsed-array]
;  (println parsed-array)
  (let [[entry-type & rvals] parsed-array]
    rvals))

(defn line-type
  "Returns the type of statement a given line is (intended for looking ahead etc.)"
  [line grammar]
;  (println "type!: " line)
  (first
    (first
      (ip/parse grammar line))))

(defn join-multiline-array
  "join three line array statement"
  [text]
;  (println text)
  (s/join
    (list
      (s/trim
        (s/trim-newline
          (nth text 0)))
      (s/trim
        (s/trim-newline
          (nth text 1)))
      (s/trim
        (s/trim-newline
          (nth text 2))))))

(defn trim-all
  [string]
  (if (empty? string)
    ""
    (s/trim
      (s/trim-newline
        string))))

(declare pS pA pC)

(defn pS
    "Process a single statement"
  [text grammar dict]
;  (println "pS!")
  (let [line (trim-all (first text))
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)]
;    (println "line: " line "parsed: " parsed "dict: " dict)
    [(conj (map-statement parsed) dict) (rest text)]))

(defn pA
    "Process an array statement"
  [text grammar dict]
;  (println "pA!")
  (let [line (trim-all (first text))
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)]
    [(conj (array-statement parsed) dict) (rest text)]))

(defn pEA
    "Process an array statement"
  [text grammar dict]
;  (println "pEA!")
  (let [line (trim-all (first text))
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)]
    [(end-array-statement parsed) (rest text)]))

(defn pAE
    "Process a multi-line array statement"
  [text grammar dict]
;  (println "pAE!")
  (let [line (join-multiline-array text)
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)]
;    (println line)
    [(conj (array-statement parsed) dict) (drop 3 text)]))

(defn pN
    "Process a statement that opens a new section"
  [text grammar dict]
; (println "pN!")
  (let [line (trim-all (first text))
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)
        [pC-dict pC-text] (pC (rest text) grammar {})]
    [(conj {(nested-keyword parsed) pC-dict} dict) pC-text]))


(defn pC
  "Main parser function"
  [text grammar dict]
  (let [line (trim-all (first text))
        parsed (first (ip/parse grammar line))
        parsed-type (first parsed)]

;    (println "next line: " line "type: " parsed-type)
;    (println "pC!")
    (condp = parsed-type
      :STATEMENT (let [[newtree newtext] (pS text grammar dict)]
                   (recur newtext grammar newtree))
      :LPARTIAL (let [[newtree newtext] (pN text grammar dict)]
                    (recur newtext grammar newtree))
      :OB (recur (rest text) grammar dict)
      ;:CB (recur (rest text) grammar dict)
      :CB [dict (rest text)]
      ;:ARRAY (let [[newtree newtext] (pA text grammar dict)]
       ;         (recur newtext grammar newtree))
      :ENDARRAY (let [[newtree newtext] (pEA text grammar dict)]
                ;(recur newtext grammar newtree))
                  [newtree newtext])
      :ARRAY (let [[newtree newtext] (pA text grammar dict)]
                  (recur newtext grammar newtree))
      ;:ENDARRAY [dict (rest text)]
      :NULL [dict text])))

