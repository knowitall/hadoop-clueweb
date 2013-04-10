package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.chunk.OpenNlpChunker

object CorpusChunkerMain extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0)
    val output = args(1)

    // initialize chunker
    lazy val chunker = new OpenNlpChunker()

    // chunk and save
    val lines: DList[String] = fromTextFile(input).flatMap { line: String =>
      try {
        val sentence = implicitly[TabFormat[CluewebSentence]].read(line)
        val chunked = chunker(line)
        Some(implicitly[TabFormat[ChunkedCluewebSentence]].write(new ChunkedCluewebSentence(sentence, chunked)))
      }
      catch {
        case e: Throwable => 
          System.err.println("Failure on line: " + line)
          e.printStackTrace()
          None
      }
    }

    try {
      persist(toTextFile(lines, output, overwrite=true))
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }

  def usage() {
    System.err.println("Usage: hadoop jar <this.jar> <inputfile> <outputfile>");
    System.exit(0);
  }
}
