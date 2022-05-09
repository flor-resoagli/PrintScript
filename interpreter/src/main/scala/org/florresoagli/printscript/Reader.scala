package org.florresoagli.printscript
import java.io.File
import java.util.Scanner
import scala.io.Source

trait Reader {
  def readSingleLine(): String
}

case class ConsoleIReader() extends Reader {
  def readSingleLine(): String = {
    new Scanner(System.in).nextLine()

  }
}

case class FakeIReader(file: String) extends Reader {
  def readSingleLine(): String = {
    Source.fromFile(file).getLines.next
  }
}
