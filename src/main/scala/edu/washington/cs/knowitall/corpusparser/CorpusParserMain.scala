package edu.washington.cs.knowitall.corpusparser

import com.nicta.scoobi.Scoobi._
import java.io.File

object CorpusParserMain extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0);
    val output = args(1);

    // lazily initialize malt parser
    // lazy val malt = new MaltParser(getClass().getResource("engmalt.linear-1.7.mco"), None)

    val lines = fromTextFile(input).filter(x => x != "");

    try {
      val graphs = lines.map(line => line)
      persist(toTextFile(graphs, output))
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }

  def usage() {
    System.err.println("Usage: hadoop jar <this.jar> <inputfile> <outputfile>");
    System.exit(0);
  }
}
