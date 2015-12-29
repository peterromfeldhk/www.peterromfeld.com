(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies
  '[[hiccup "1.0.5"]
    [perun "0.2.2-SNAPSHOT"]
    [pandeiro/boot-http "0.7.0"]
    [org.martinklepsch/boot-gzip "0.1.2"]
    [org.martinklepsch/boot-garden "1.2.5-8"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]]
         '[org.martinklepsch.boot-gzip :refer [gzip]]
         '[org.martinklepsch.boot-garden :refer :all])

(task-options!
  pom {:project 'www.peterromfeld.com
       :version "0.1.0"})

(deftask build-dev
         "Build blog dev version"
         []
         (comp (global-metadata)
               (base)
               (markdown)
               (garden :styles-var 'homepage.styles/base
                       :output-to "public/css/site.css"
                       :pretty-print true)
               (collection :renderer 'homepage.index/render :page "index.html")))

(deftask build
         "Build blog prod version."
         []
         (comp (build-dev)
               (gzip :regex [#".html$" #".css$" #".js$"])
               ; (s3-sync)
               ))

(deftask dev
         []
         (comp
           (watch)
           (build-dev)
           (serve :resource-root "public" :port 3103)
           ))