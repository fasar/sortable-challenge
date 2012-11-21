package fasar.sortable.challenge.model

case class Price(currency: Currency, value: Double) {
  def getUsdPrice: Double = {
    if (currency == Currency.getUsd) value
    else (value * currency.usdPice)
  }

  def convertTo(curr: Currency): Price = {
    if (curr == currency) this
    else {
      val usd = getUsdPrice
      val value = usd / curr.usdPice
      Price(curr, value)
    }
  }
}

