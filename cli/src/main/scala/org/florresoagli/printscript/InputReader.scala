package org.florresoagli.printscript

import java.io.File
import scala.collection.Iterator
import scala.io.Source

trait InputReader {  def read(): String }



class FileReader(src: String) extends InputReader  {

  override def read(): String = open(src).read()


  def open(path: String) = new File(path)

  implicit class RichFile(file: File) {
     def read(): String =
       val source = try Source.fromFile(file)   catch{
         case e: java.io.FileNotFoundException => error("File not found")
       }
       try source.getLines.mkString("\n") finally source.close()

   }

}

class ConsoleReader extends InputReader {
  override def read(): String = scala.io.StdIn.readLine()
}

class FileReaderAdapter(file: File) extends InputReader {
  override def read(): String = file.read()

  implicit class RichFile(file: File) {
    def read(): String =
      val source = Source.fromFile(file)
      try source.getLines.mkString("\n") finally source.close()

  }

  def error(msg: String) = throw new Exception(msg)
}
