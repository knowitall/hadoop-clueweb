package edu.knowitall.hadoop.models

case class CluewebSentence(id: String, url: String, date: String, index: Int, text: String)

object CluewebSentence {
  implicit def formatter = TabFormat

  object TabFormat extends TabFormat[CluewebSentence] {
    def write(sentence: CluewebSentence) = {
      val fields = CluewebSentence.unapply(sentence).get.productIterator.map(_.toString).toSeq
      SeqTabFormat.write(fields)
    }

    def read(pickle: String) = {
      pickle.split("\t") match {
        case Array(id, url, date, index, text, _ @ _*) => CluewebSentence(id, url, date, index.toInt, text)
      }
    }
  }
}
