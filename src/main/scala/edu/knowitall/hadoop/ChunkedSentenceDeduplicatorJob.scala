package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.parse.ClearParser
import com.nicta.scoobi.io.text.TextInput
import com.nicta.scoobi.io.text.TextSource
import com.hadoop.mapreduce.LzoTextInputFormat

object ChunkedSentenceDeduplicatorJob extends ScoobiApp {

  def run() {
    if (args.length != 2) usage

    // get the cl arguments
    val input = args(0)
    val output = args(1)

    // initialize chunker
    lazy val parser = new ClearParser()

    // chunk and save
    val lines: DList[(String, String)] = TextInput.fromTextSource(
      new TextSource(
        Seq(input),
        inputFormat = classOf[LzoTextInputFormat].asInstanceOf[Class[org.apache.hadoop.mapreduce.lib.input.TextInputFormat]])).flatMap { line: String =>
      try {
        val sentence = implicitly[TabFormat[ChunkedCluewebSentence]].read(line)
        if (sentence.text.size < 300) {
          Some((sentence.text.trim.toLowerCase, line))
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

    val grouped = lines.groupByKey.map { case (text, insts) =>
      val head = insts.head
      val urls = insts.tail.flatMap{inst =>
        implicitly[TabFormat[ChunkedCluewebSentence]].read(inst).url
      }
      (Iterable(head) ++ urls).mkString("\t")
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
