package fasar.sortable.challenge.json

import scala.util.matching.Regex.Match
import scala.collection.immutable.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: fabien
 * Date: 30/05/13
 * Time: 00:15
 * To change this template use File | Settings | File Templates.
 */
object FasarJson {

  // regex json parser hand made
//  def parseLine(line: String): List[String] = {
//    // catch keys values
//    val reg1 = """"([^:]*)":"((?:(?:(?<=\\)")|[^"])*)"""".r
//    val list: Iterator[Match] = reg1.findAllMatchIn(line)
//    (for (keyval <- list if keyval.groupCount > 0)
//    yield {
//      unEscapeJson(keyval.subgroups(1))
//    }).toList
//  }

  // split and cut parser hand made
  def parseLine(line: String): List[String] = {
    val res = line.substring(2, line.size-2).split("\",\"|\":\"")
    var i = -1
    println(line)
    (for((elem, i) <- res.zipWithIndex
         if( i % 2 == 1))
    yield {
      elem
    }).toList
  }



  /** unescape a string json compatible
    *
    * @param src   the string to process compatible with json (with escaped cara)
    * @return      The unescaped string
    */

  private def unEscapeJson (src: String): String = {
    val escape:List[(String, String)] =
      List("""\"""" -> """"""",
        """\\""" -> """\""",
        """\/""" -> """/""",
        """\b""" -> "\b",
        """\f""" -> "\f",
        """\n""" -> "\n",
        """\r""" -> "\r",
        """\t""" -> "\t")

    escape.foldRight(src) {case ((elemEscape, replacement), src) =>
      src.replace(elemEscape, replacement)
    }
  }
}



