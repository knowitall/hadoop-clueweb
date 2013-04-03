package edu.knowitall.hadoop.models

trait TabWriter[T] {
  def write(item: T): String
}

trait TabReader[T] {
  def read(string: String): T
}

trait TabFormat[T] extends TabWriter[T] with TabReader[T]