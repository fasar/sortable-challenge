package fasar.sortable.challenge.model


/** Represents an item
  *
  * An Item object represent a line of listing
  *
  * case class have equals, hashCode and toString functions
  *
  * @param title          title of an item
  * @param manufacturer   manufacturer of an item
  * @param currency       currency of an item
  * @param price          price of an item
  *
  * @author Fabien Sartor
  */
case class Item(title: String,
                manufacturer: String,
                currency: String,
                price: String) {
  
  def getPrice: Option[Price] = {
    try {
      val priceValue = this.price.toDouble
      val currency = Currency.getWithSymbol(this.currency)
      if(currency.isDefined) Some(Price(currency.get, priceValue))
      else None
    } catch {
      case e:Exception => None
    }
    
  }
}

/**
 * Item object to build item.
 */
object Item {
  /** get a product from a Map[String, String]
    *
    * This kind of map is returned by the json parser
    *
    * @param param  param is a map of  "object-name"->"object-value"
    * @return       return an item
    */
  def getItem(param: Map[String, String]) = {
    val title = param.get("title").getOrElse("")
    val manufacturer = param.get("manufacturer").getOrElse("")
    val currency = param.get("currency").getOrElse("")
    val price = param.get("price").getOrElse("")
    Item(title, manufacturer, currency, price)
  }
}
