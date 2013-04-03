organization := "edu.washington.cs.knowitall.common-scala"

name := "hadoop-clueweb"

libraryDependencies ++= Seq(
    "org.apache.avro" % "avro" % "1.5.4",
    "com.nicta" %% "scoobi" % "0.7.3-RELEASE-TRIAL-cdh4",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-chunk-opennlp" % "2.4.0",
    "org.scalaz" %% "scalaz-core" % "6.0.4",
    "junit" % "junit" % "4.11" % "test",
    "org.specs2" %% "specs2" % "1.12.3" % "test"
    )

resolvers ++= Seq("nicta's avro" at "http://nicta.github.com/scoobi/releases",
    "cloudera" at "https://repository.cloudera.com/content/repositories/releases")
