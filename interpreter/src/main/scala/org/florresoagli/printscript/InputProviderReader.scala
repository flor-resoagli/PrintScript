package org.florresoagli.printscript
import java.io.File
import java.util.Scanner
import scala.io.Source

trait InputProviderReader {
  def readSingleLine(): String
}

case class ConsoleIReader() extends InputProviderReader {
  def readSingleLine(): String = {
    new Scanner(System.in).nextLine()

  }
}

case class FakeIReader(file: String) extends InputProviderReader {
  def readSingleLine(): String = {
    Source.fromFile(file).getLines.next
  }
}
