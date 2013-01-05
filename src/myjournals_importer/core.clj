(ns myjournals-importer.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [java.text DecimalFormat SimpleDateFormat]))

(defn read-csv
  "Read the filepath as a CSV and forward it to func."
  [fpath func]
  (with-open [in-file (io/reader fpath :encoding "Shift_JIS")]
    (doall
     (func (csv/read-csv in-file)))))

;; (with-open [out-file (io/writer "out-file.csv")]
;;   (csv/write-csv out-file
;;                  [["abc" "def"]
;;                   ["ghi" "jkl"]]))

(defn to-date
  [str]
  (.parse (SimpleDateFormat. "yyyy/MM/dd") str))

(defn from-date
  [dt]
  (.format (SimpleDateFormat. "yyyy/MM/dd") dt))

(defn is-date
  [str]
  (.matches str "\\d+[/-]\\d+[/-]\\d+"))

(defn to-int
  [str]
  (.parse (DecimalFormat. "0,000") str))

(defn compose-line-mufg
  [l]
  (let [date     (l 0)
        tekiyou  (l 1)
        naiyou   (l 2)
        shiharai (l 3)
        azukari  (l 4)
        zandaka  (l 5)
        memo     (l 6)
        kubun    (l 7)
        inout    (l 8)
        detail (.trim (str tekiyou " " naiyou))
        account "Assets:Bank:Tamachi"
        dorc   (empty? azukari) ; t:expense, f:revenue
        debit  (if dorc "MUFGt:Shiharai" account)
        credit (if dorc account "MUFGt:Azukari")
        amount (if dorc shiharai azukari)]
    (format (str "fossil ticket add"
                 " title '%s: %s'"
                 " type 'Accounting'"
                 " status 'Closed'"
                 " debit '%s'"
                 " credit '%s'"
                 " amount %s"
                 "%s")
            (from-date (to-date date))
            detail
            debit
            credit
            (to-int amount)
            (if (empty? memo) "" (str " comment '" memo "'")))))

(defn compose-line-visa
  [l]
  (let [date     (l 0)
        shop     (l 1)
        riyou    (l 2)
        kubun    (l 3)
        kaisuu   (l 4)
        shiharai (l 5)
        misc     (l 6)
        debit   "SMBCv:Goriyou"
        credit  "Liabilities:Visa"
        amount  (if (empty? riyou) shiharai riyou)]
    (format (str "fossil ticket add"
                 " title '%s: %s'"
                 " type 'Accounting'"
                 " status 'Closed'"
                 " debit '%s'"
                 " credit '%s'"
                 " amount %s"
                 "%s")
            (from-date (to-date date))
            shop
            debit
            credit
            (to-int amount)
            (if (empty? misc) "" (str " comment '" misc "'")))))

(defn map-lines
  [lines func]
  (map func (filter #(is-date (first %)) lines)))

(defn print-lines
  [fpath complinef]
  (read-csv fpath (fn [ls] (map-lines ls #(println (complinef %))))))

(defn write-lines
  [infile outfile complinef]
  (with-open [writer (io/writer outfile)]
    (read-csv infile (fn [ls]
                       (map-lines ls #(do (.write writer (complinef %))
                                          (.newLine writer)))))))
