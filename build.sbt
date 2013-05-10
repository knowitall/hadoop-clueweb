import AssemblyKeys._
assemblySettings

organization := "edu.washington.cs.knowitall.common-scala"

name := "hadoop-clueweb"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-chunk-opennlp" % "2.4.1",
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-parse-clear" % "2.4.1" exclude("com.google.clearnlp", "clearnlp"),
    "com.googlecode.clearnlp" % "clearnlp-threadsafe2" % "1.3.0")

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.2"

resolvers ++= Seq("nicta" at "http://nicta.github.com/scoobi/releases",
                  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
                  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "internal maven" at "http://knowitall.cs.washington.edu/maven2",
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
