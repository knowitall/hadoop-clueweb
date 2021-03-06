package edu.knowitall.hadoop

import edu.knowitall.hadoop.models._
import java.io.File
import edu.knowitall.tool.tokenize.Tokenizer
import edu.knowitall.tool.postag.ClearPostagger
import edu.knowitall.tool.parse.ClearParser
import edu.knowitall.common.Resource
import scala.io.Source
import java.io.BufferedWriter
import java.io.PrintWriter
import java.io.OutputStreamWriter
import edu.knowitall.common.Timing
import java.util.zip.GZIPOutputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object CorpusParserMain extends App {
  final val GROUP_SIZE = 10000

  case class Config(inputFile: Option[File], outputFile: Option[File]) {
    def writer() = outputFile match {
      case Some(file) => new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"), false)
      case None =>
        new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8")), false)
    }
    def source() = inputFile match {
      case Some(file) => Source.fromFile(file, "UTF-8")
      case None => Source.fromInputStream(System.in, "UTF-8")
    }
  }

  class PrinterActor(config: Config) extends Actor {
    val writer = config.writer()

    def receive = {
      case 'flush => writer.flush()
      case line => writer.println(line)
    }

    override def postStop() = writer.close()
  }

  if (args.length == 0) run(Config(None, None))
  else if (args.length == 2) run(Config(Some(new File(args(0))), Some(new File(args(1)))))
  else throw new IllegalArgumentException("0 or 2 args")

  def run(config: Config) {
    // initialize chunker
    val tokenizer = new Tokenizer {
      override def tokenize(string: String) = Tokenizer.computeOffsets(string.split("\\s+").toSeq, string)
    }
    lazy val postagger = new ClearPostagger(tokenizer)
    lazy val parser = new ClearParser(postagger)

    val system = ActorSystem("MySystem")

    // chunk and save
    var index = 0
    var sumns: Long = 0
    val start = System.nanoTime
      val writerActor = system.actorOf(Props(new PrinterActor(config)), name = "printer")
      Resource.using(config.source()) { source =>
        for (group <- source.getLines.grouped(GROUP_SIZE)) {
          index += 1
          System.err.println("Time since start: " + Timing.Seconds.format(System.nanoTime - start))
          System.err.println("Processing " + index * GROUP_SIZE + "...")

          Timing.timeThen {
            for (line <- group.par) {
              try {
                val sentence = implicitly[TabFormat[ChunkedCluewebSentence]].read(line)
                if (sentence.text.size < 300) {
                  val parsed =
                    Timing.timeThen {
                      parser(sentence.tokens)
                    } { ns =>
                      if (ns / Timing.Seconds.divisor > 1) {
                        System.err.println("Long parse (" + Timing.Seconds.format(ns) + "): " + line)
                      }
                    }
                  writerActor ! (implicitly[TabFormat[ParsedCluewebSentence]].write(new ParsedCluewebSentence(sentence, parsed)))
                }
              } catch {
                case e: Throwable =>
                  System.err.synchronized {
                    System.err.println("Failure on line: " + line)
                    e.printStackTrace()
                    None
                  }
              }
            }
          } { ns =>
            sumns += ns
            System.err.println("Completed in " + Timing.Seconds.format(ns) + " (average is " + Timing.Seconds.format(sumns / index) + ").")
          }
        }
      }

    System.err.println("Final flush...")
    writerActor ! 'flush
    system.shutdown()
    System.err.println("done.")
  }
}
