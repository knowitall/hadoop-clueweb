package edu.knowitall.hadoop

import com.nicta.scoobi.Scoobi._
import java.io.File

object Copier extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0)
    val output = args(1)

    // identity map
    val lines: DList[String] = fromTextFile(input).map { line: String => line }

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
