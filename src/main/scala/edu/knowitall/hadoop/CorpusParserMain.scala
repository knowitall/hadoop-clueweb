package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.parse.ClearParser

object CorpusParserMain extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0)
    val output = args(1)

    // initialize chunker
    lazy val parser = new ClearParser()

    // chunk and save
    val lines: DList[(String, Unit)] = fromTextFile(input).flatMap { line: String =>
      try {
        val sentence = implicitly[TabFormat[ChunkedCluewebSentence]].read(line)
        if (sentence.text.size < 300) {
          val parsed = parser(sentence.text)
          Some((implicitly[TabFormat[ParsedCluewebSentence]].write(new ParsedCluewebSentence(sentence, parsed)), ()))
        }
        else {
          None
        }
      }
      catch {
        case e: Throwable => 
          System.err.println("Failure on line: " + line)
          e.printStackTrace()
          None
      }
    }

    try {
      persist(toTextFile(lines.map(_._1), output, overwrite=false))
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }

  def usage() {
    System.err.println("Usage: hadoop jar <this.jar> <inputfile> <outputfile>");
    System.exit(0);
  }
}
