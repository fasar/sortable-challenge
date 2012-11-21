package fasar.sortable.challenge.converter.impl

import fasar.sortable.challenge.model._
import scala.collection.mutable.StringBuilder
import fasar.sortable.challenge.converter.Converter

object CsvConverter extends Converter {

  /** Create a CSV file of links
    *
    * It get the JSon data of a list of links. It create
    * A link is a relation between a product and a listing
    *
    * @param link  the link
    * @return      content of CSV file
    */
  def convert(links: List[Link]): String = {
    val res = links.map { x => convert(x) }
    res.mkString("\n")
  }

  /** get the JSon data of a link
    *
    * a link is a relation between a product and a listing
    *
    * @param link  the link
    * @return      json data
    */
  def convert(link: Link): String = {
    val str = new StringBuilder
    str append link.product.name
    str append ";"
    str append getPrices(link.items).mkString(";")
    str toString
  }

  /** get a sorted list of prices with of a list items
    *
    * @param items  param
    * @retrun       the sorted list of prices
    */
  private def getPrices(items: List[Item]): List[Double] = {
    val res =
      (for (
        item <- items;
        price <- item.getPrice
      ) yield {
        price.getUsdPrice
      })
    res.sortWith(_ < _)
  }
}