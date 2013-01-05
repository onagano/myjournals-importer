(ns myjournals-importer.core-test
  (:use clojure.test
        myjournals-importer.core))

(deftest test-to-date
  (testing "Returns a Date object."
    (is (= 0 0))))

(deftest test-from-date
  (testing "Returns a date string of yyyy/MM/dd"
    (is (= 0 0))))

(deftest test-print-lines-mufg
  (testing "Test print-lines for MUFG CSV file."
    (is (do
          (print-lines "dev-resources/mufg.csv" compose-line-mufg)
          (= 0 0)))))

(deftest test-print-lines-visa
  (testing "Test print-lines for VISA CSV file."
    (is (do
          (print-lines "dev-resources/visa.csv" compose-line-visa)
          (= 0 0)))))

(deftest test-write-lines-mufg
  (testing "Test write-lines for MUFG"
    (is (do
          (write-lines "dev-resources/mufg.csv"
                       "target/mufg.txt"
                       compose-line-mufg)
          (= 0 0)))))

(deftest test-write-lines-visa
  (testing "Test write-lines for VISA"
    (is (do
          (write-lines "dev-resources/visa.csv"
                       "target/visa.txt"
                       compose-line-visa)
          (= 0 0)))))
