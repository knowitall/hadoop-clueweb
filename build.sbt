import AssemblyKeys._
assemblySettings

organization := "edu.washington.cs.knowitall.common-scala"

name := "hadoop-clueweb"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq("com.nicta" % "scoobi_2.10" % "0.7.0-RC1-cdh3",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-chunk-opennlp" % "2.4.2",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-parse-clear" % "2.4.2",
    "org.apache.hadoop" % "hadoop-lzo" % "0.4.13")

resolvers ++= Seq("nicta" at "http://nicta.github.com/scoobi/releases",
                  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
                  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "cloudera" at "https://repository.cloudera.com/content/repositories/releases")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x => {
      val oldstrat = old(x)
      if (oldstrat == MergeStrategy.deduplicate) MergeStrategy.first
      else oldstrat
    }
  }
}
