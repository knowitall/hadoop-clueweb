package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.chunk.OpenNlpChunker

object DeduplicatorMain extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0);
    val output = args(1);

    // chunk and save
    val lines: DList[(String, Int)] = fromTextFile(input).map { 
      line: String => (line, 1)
    }
    
    val grouped: DList[(String, Iterable[Int])] = lines.groupByKey
    
    val summed: DList[(String, Int)] = grouped.combine(_ + _)

    val outputString: DList[String] = grouped.map { case (a, b) => a + "\t" + b }

    try {
      persist(toTextFile(outputString, output, overwrite=true))
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }

  def usage() {
    System.err.println("Usage: hadoop jar <this.jar> <inputfile> <outputfile>");
    System.exit(0);
  }
}
