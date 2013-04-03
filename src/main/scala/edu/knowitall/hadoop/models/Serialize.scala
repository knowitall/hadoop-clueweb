package edu.knowitall.hadoop.models

object SeqTabFormat extends TabFormat[Seq[String]] {
  def write(seq: Seq[String]) = {
    seq foreach (field => require(!field.contains("\t"), "field contains tab: " + field))
    seq.mkString("\t")
  }

  def read(pickle: String) = {
    pickle.split("\t")
  }
}