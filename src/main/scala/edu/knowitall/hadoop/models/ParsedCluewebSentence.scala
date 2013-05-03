package edu.knowitall.hadoop.models

import edu.knowitall.tool.chunk.ChunkedToken
import edu.knowitall.tool.tokenize.Tokenizer

import edu.knowitall.tool.parse.graph.DependencyGraph

case class ParsedCluewebSentence(id: String, url: String, date: String, index: Int, text: String, tokens: String, postags: String, chunks: String, dgraphString: String) {
  def this(sentence: ChunkedCluewebSentence, dgraph: DependencyGraph) = {
    this(sentence.id, sentence.url, sentence.date, sentence.index, sentence.text, sentence.tokens, sentence.postags, sentence.chunks, dgraph.serialize)
  }

  def chunkedSentence = ChunkedCluewebSentence(id, url, date, index, text, tokens, postags, chunks)

  def dgraph: DependencyGraph = DependencyGraph.deserialize(dgraphString)
}

object ParsedCluewebSentence {
  implicit def formatter = TabFormat

  object TabFormat extends TabFormat[ParsedCluewebSentence] {
    def write(sentence: ParsedCluewebSentence) = {
      val fields = ParsedCluewebSentence.unapply(sentence).get.productIterator.map(_.toString).toSeq
      SeqTabFormat.write(fields)
    }

    def read(pickle: String) = {
      pickle.split("\t") match {
        case Array(id, url, date, index, text, tokens, postags, chunks, dgraph, _ @ _*) => ParsedCluewebSentence(id, url, date, index.toInt, text, tokens, postags, chunks, dgraph)
      }
    }
  }
}
