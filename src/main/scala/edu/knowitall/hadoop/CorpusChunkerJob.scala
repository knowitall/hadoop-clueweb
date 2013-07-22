package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.chunk.Chunker
import edu.knowitall.tool.chunk.OpenNlpChunker

object CorpusChunkerJob extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0)
    val output = args(1)

    // initialize chunker
    lazy val chunker = new OpenNlpChunker()

    // chunk and save
    val lines: DList[(String, Unit)] = fromTextFile(input).flatMap { line: String =>
      try {
        val sentence = implicitly[TabFormat[CluewebSentence]].read(line)
        if (sentence.text.size < 300) {
          val chunked = Chunker.joinOf(chunker(sentence.text))
          Some((implicitly[TabFormat[ChunkedCluewebSentence]].write(new ChunkedCluewebSentence(sentence, chunked)), ()))
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

    val grouped = lines.groupByKey.map(_._1)

    try {
      persist(toTextFile(grouped, output, overwrite=false))
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }

  def usage() {
    System.err.println("Usage: hadoop jar <this.jar> <inputfile> <outputfile>");
    System.exit(0);
  }
}
