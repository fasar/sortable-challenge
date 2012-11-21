package fasar.sortable.challenge.business

import fasar.sortable.challenge.model._
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


object PriceFilter {
  private val log: Log = LogFactory.getLog(this.getClass)

  def getAveragePrice(link:Link, currency:Currency) = {
    val averagePriceUsd = getAverageUsdPrice(link)
    averagePriceUsd.convertTo(currency)
  }
    
  def getAverageUsdPrice(link:Link) = {
    val prices = getPricesList(link)
    
    val averageUsd = average(prices)
    Price(Currency.getUsd, averageUsd)
  }
  
  def getPricesList(link:Link) = 
      for(item <- link.items;
    	  price <- getPrice(item))
      yield { 
        price.getUsdPrice 
      }
  
  def average(list: List[Double]):Double = {
    val size = list.size
    list.foldLeft(0.0)((old, nb) => old + nb / size)
  }
  
  
  def getStardarDeviationUsdPrice(link:Link):Double = {
    val prices = getPricesList(link)
    val averageUsd = getAverageUsdPrice(link)
    standardDeviation(prices, averageUsd.value)
  }
  
  def standardDeviation(list: List[Double], average:Double):Double = {
    val size = list.size
    val res = list.foldLeft(0.0)((old, nb) => old + (nb * nb / size) ) - average * average
    math.sqrt(res)
  }
  
  
  def getPrice(item:Item): Option[Price] = {
    val currency = Currency.getWithSymbol(item.currency)
    val price = try { Some(item.price.toDouble) } catch {case e:Exception => None}
    
    if(currency.isDefined && price.isDefined) Some(Price(currency.get, price.get))
    else None
  }
  
  
}