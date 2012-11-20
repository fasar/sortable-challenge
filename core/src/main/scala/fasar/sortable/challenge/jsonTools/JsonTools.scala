package fasar.sortable.challenge.jsonTools

import fasar.sortable.challenge.model._

import scala.collection.mutable.StringBuilder

/** Some tools to generate Json data
  *
  * @author Fabien Sartor
  */
object JsonTools {

  /** get the JSon data of a link
    *
    * a link is a relation between a product and a listing
    *
    * @param link  the link
    * @return      json data
    */
  def getJson(link: Link): String = {
    val str = new StringBuilder
    str append """{"product-name":""""
    str append escapeJson(link.product.name)
    str append """","listings":"""
    str append getJsonItems(link.items)
    str append "}"
    str toString
  }

  /** get json data of an list of item
    *
    * @param items list of item
    * @return json data
    */
  private def getJsonItems(items: List[Item]): String = {
    val itemsJson =
      for (item <- items)
      yield {
        getJsonItem(item)
      }

    val str = new StringBuilder
    str append "["
    str append itemsJson.mkString(",")
    str append "]"
    str toString
  }


  /** get json data of an item
    *
    * @param item the item
    * @return json data of the item
    */
  private def getJsonItem(item: Item): String = {
    val str = new StringBuilder

    str append """{"title":""""
    str append escapeJson(item.title)
    str append """","manufacturer":""""
    str append escapeJson(item.manufacturer)
    str append """","currency":""""
    str append escapeJson(item.currency)
    str append """","price":""""
    str append escapeJson(item.price)
    str append """"}"""
    str toString
  }

  /** Escape a string to be json compatible
    *
    * @param src   the string to process
    * @return      compatible json string
    */
  private def escapeJson(src: String): String = {
    val res =
      src.map {
        (x: Char) =>
          x match {
            case '"' => """\""""
            case '\\' => """\\"""
            case '/' => """\/"""
            case '\b' => """\b"""
            case '\f' => """\f"""
            case '\n' => """\n"""
            case '\r' => """\r"""
            case '\t' => """\t"""
            case e => e.toString()
          }
      }
    res.mkString
  }
}
