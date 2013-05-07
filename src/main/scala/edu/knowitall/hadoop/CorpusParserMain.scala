package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import com.nicta.scoobi.Scoobi._
import java.io.File
import edu.knowitall.tool.parse.ClearParser
import edu.knowitall.common.Resource
import scala.io.Source
import java.io.PrintWriter
import edu.knowitall.common.Timing
import java.util.zip.GZIPOutputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream

object CorpusParserMain extends App {
  final val GROUP_SIZE = 50000

  case class Config(inputFile: File, outputFile: File) {
    def writer() = new PrintWriter(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile))))
    def source() = Source.fromFile(inputFile, "UTF-8")
  }

  def run(config: Config) {
    // initialize chunker
    lazy val parser = new ClearParser()

    // chunk and save
    Resource.using(config.writer()) { writer =>
      Resource.using(config.source()) { source =>
        for {
          group <- source.getLines.grouped(GROUP_SIZE)
          line <- group
        } {
          try {
            val sentence = implicitly[TabFormat[ChunkedCluewebSentence]].read(line)
            if (sentence.text.size < 300) {
              val parsed =
                Timing.timeThen {
                  parser(sentence.text)
                } { ns =>
                  if (ns / Timing.Seconds.divisor > 1) {
                    System.err.println("Long parse (" + Timing.Seconds.format(ns) + "): " + line)
                  }
                }
              writer.synchronized {
                writer.println(implicitly[TabFormat[ParsedCluewebSentence]].write(new ParsedCluewebSentence(sentence, parsed)))
              }
            }
          } catch {
            case e: Throwable =>
              System.err.println("Failure on line: " + line)
              e.printStackTrace()
              None
          }
        }
      }
    }
  }
}
