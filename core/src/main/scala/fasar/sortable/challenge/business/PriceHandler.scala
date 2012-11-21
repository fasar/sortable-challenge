package fasar.sortable.challenge.business

import fasar.sortable.challenge.model._
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

object PriceHandler {
  private val log: Log = LogFactory.getLog(PriceHandler.this.getClass)

  object Math {
    def average(list: List[Double]): Double = {
      val size = list.size
      list.foldLeft(0.0)((old, nb) => old + nb / size)
    }

    def standardDeviation(list: List[Double], average: Double): Double = {
      val size = list.size
      val res = list.foldLeft(0.0)((old, nb) => old + (nb * nb / size)) - average * average
      math.sqrt(res)
    }
  }

  def averagePrice(link: Link, currency: Currency) = {
    val averagePriceUsd = averageUsdPrice(link)
    averagePriceUsd.convertTo(currency)
  }

  def averageUsdPrice(link: Link) = {
    val prices = getUsdPricesList(link)

    val averageUsd = Math.average(prices)
    Price(Currency.getUsd, averageUsd)
  }

  private def getUsdPricesList(link: Link) =
    for (
      item <- link.items;
      price <- getPrice(item)
    ) yield {
      price.getUsdPrice
    }

  private def getPrice(item: Item): Option[Price] = {
    val currency = Currency.getWithSymbol(item.currency)
    val price = try { Some(item.price.toDouble) } catch { case e: Exception => None }

    if (currency.isDefined && price.isDefined) Some(Price(currency.get, price.get))
    else None
  }

  def standardDeviationUsdPrice(link: Link): Double = {
    val averageUsd = averageUsdPrice(link)
    standardDeviationUsdPrice(link, averageUsd.value)
  }

  def standardDeviationUsdPrice(link: Link, averageUsd: Double): Double = {
    val usdPrices = getUsdPricesList(link)
    Math.standardDeviation(usdPrices, averageUsd)
  }

}