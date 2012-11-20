package fasar.sortable.challenge.model


/** Represents a products
  *
  * An Item object represent a line of listing
  *
  * case class have equals, hashCode and toString functions
  *
  * @param name           name of the product
  * @param manufacturer   manufacturer of the product
  * @param model          model of the product
  * @param announcedDate   announcedDate of the product
  * @param family         family (optional) of the product
  *
  * @author Fabien Sartor
  */
case class Product(
                    name: String,
                    manufacturer: String,
                    model: String,
                    announcedDate: String,
                    family: String = "") {

  def hasFamily = family.size > 0

  def isMoreInformativeThan(p: Product): Boolean = {
    val sizeThisModelNb = this.model.foldLeft(0) {
      (nb, c) => nb + charWeight(c)
    }
    val sizePModelNb = p.model.foldLeft(0) {
      (nb, c) => nb + charWeight(c)
    }

    if (sizeThisModelNb > sizePModelNb) true
    else if (sizeThisModelNb < sizePModelNb) false
    else if (this.model.size > p.model.size) true
    else if (this.model.size < p.model.size) false
    else if (this.hasFamily && !p.hasFamily) true
    else if (!this.hasFamily && p.hasFamily) false
    else this.name > p.name
  }

  def charWeight(c: Char) = {
    if (c >= '0' && c <= '9') 1
    else 0
  }
}

/**
 * Product object to build product.
 */
object Product {

  /** get a product from a Map[String, String]
    *
    * This kind of map is returned by the json parser
    *
    * @param param  param is a map of  "object-name"->"object-value"
    * @return       return a product
    */
  def getProduct(param: Map[String, String]) = {
    val name = param.get("product_name").getOrElse("")
    val manufacturer = param.get("manufacturer").getOrElse("")
    val model = param.get("model").getOrElse("")
    val annoncedDate = param.get("announced-date").getOrElse("")
    val family = param.get("family").getOrElse("")
    Product(name, manufacturer, model, annoncedDate, family)
  }
}
