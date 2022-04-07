import org.rogach.scallop._
import java.util.Scanner

class CLI(arguments: Seq[String]) extends ScallopConf(arguments) {
  val apples = opt[Int](required = true)
  val bananas = opt[Int]()
  val name = trailArg[String]()
  verify()
}

object Main {
  def main(args: Array[String]) =
    val scanner = new Scanner(System.in)
    val arg = scanner.nextLine()
    val conf = new CLI("--apples 4 --bananas 10 strangeTree".split(" "))
    println("apples are: " + conf.apples())
    println(arg)
}
