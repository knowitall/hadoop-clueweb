package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.chunk.OpenNlpChunker

object CorpusChunkerMain extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0);
    val output = args(1);

    // initialize chunker
    val chunker = new OpenNlpChunker()

    // chunk and save
    val lines = fromTextFile(input).mapFlatten { line =>
      val sentence = implicitly[TabFormat[CluewebSentence]].read(line)
      val chunked = chunker(sentence.text)
      implicitly[TabFormat[ChunkedCluewebSentence]].write(new ChunkedCluewebSentence(sentence, chunked))
    }

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
