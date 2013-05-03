package edu.knowitall.hadoop.models

import edu.knowitall.tool.chunk.ChunkedToken
import edu.knowitall.tool.tokenize.Tokenizer

case class ChunkedCluewebSentence(id: String, url: String, date: String, index: Int, text: String, tokens: String, postags: String, chunks: String) {
  def this(sentence: CluewebSentence, chunkedTokens: Seq[ChunkedToken]) = {
    this(sentence.id, sentence.url, sentence.date, sentence.index, sentence.text, chunkedTokens.iterator.map(_.string).mkString(" "), chunkedTokens.iterator.map(_.postag).mkString(" "), chunkedTokens.iterator.map(_.chunk).mkString(" "))
  }

  def chunkedTokens: Seq[ChunkedToken] = {
    val tokenStrings = this.tokens.split(" ")
    val tokens = Tokenizer.computeOffsets(tokenStrings, text)
    val postags = this.postags.split(" ")
    val chunks = this.chunks.split(" ")

    val zipped = (tokens zip postags zip chunks)
    zipped.map { case ((token, postag), chunk) =>
      new ChunkedToken(chunk, postag, token.string, token.offset)
    }
  }
}

object ChunkedCluewebSentence {
  implicit def formatter = TabFormat

  object TabFormat extends TabFormat[ChunkedCluewebSentence] {
    def write(sentence: ChunkedCluewebSentence) = {
      val fields = ChunkedCluewebSentence.unapply(sentence).get.productIterator.map(_.toString).toSeq
      SeqTabFormat.write(fields)
    }

    def read(pickle: String) = {
      pickle.split("\t") match {
        case Array(id, url, date, index, text, tokens, postags, chunks, _ @ _*) => ChunkedCluewebSentence(id, url, date, index.toInt, text, tokens, postags, chunks)
      }
    }
  }
}
