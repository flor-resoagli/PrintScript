import java.io.File
import scala.collection.Iterator
import scala.io.Source

trait InputReader {  def read(): String }



class FileReader(src: String) extends InputReader  {

  override def read(): String = open(src).read()


  def open(path: String) = new File(path)

  implicit class RichFile(file: File) {
     def read(): String =
       val source = Source.fromFile(file)
       try source.getLines.mkString("\n") finally source.close()

   }

}