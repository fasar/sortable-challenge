package fasar.sortable.challenge.model

case class Currency(name: String,
                    acronym: String,
                    usdPice: Double) {

  // all currencies are located at : http://www.xe.com/iso4217.php
  //  or and to get symbol : http://www.xe.com/symbols.php
  //  all currencies with country : http://en.wikipedia.org/wiki/List_of_circulating_currencies_by_country

  //values of these currencies are http://www.x-rates.com/table/?from=USD&amount=1.00

}

object Currency {

  val currencies: Set[Currency] = CurrencyRawData.currencies
  println(currencies)

  def getWithSymbol(acronym: String): Option[Currency] = {
    currencies.collectFirst { case curr if curr.acronym == acronym => curr }
  }

  val getUsd = {
    getWithSymbol("USD").get
  }

}

private object CurrencyRawData {
  val data: Array[Array[String]] = """Emirati Dirham;AED;0.27224218664924316
Argentine Peso;ARS;0.20803129789270539
Australian Dollar;AUD;1.038107
Bulgarian Lev;BGN;0.653998283908503
Bahraini Dinar;BHD;2.653228
Bruneian Dollar;BND;0.8166231814822527
Brazilian Real;BRL;0.48027440958666145
Botswana Pula;BWP;0.12490000193595002
Canadian Dollar;CAD;1.003099
Swiss Franc;CHF;1.063494
Chilean Peso;CLP;0.0020839919616262056
Chinese Yuan Renminbi;CNY;0.16030784235583276
Colombian Peso;COP;5.505004324180897E-4
Czech Koruna;CZK;0.05052880925323978
Danish Krone;DKK;0.17182759437759493
Euro;EUR;1.281492
British Pound;GBP;1.592194
Hong Kong Dollar;HKD;0.12902731340489274
Croatian Kuna;HRK;0.16972871410980261
Hungarian Forint;HUF;0.004562068770449473
Indonesian Rupiah;IDR;1.0380982040901069E-4
Israeli Shekel;ILS;0.2563748898228911
Indian Rupee;INR;0.0181340136248098
Iranian Rial;IRR;8.164005064948742E-5
Icelandic Krona;ISK;0.007880233065773153
Japanese Yen;JPY;0.012235476642169202
South Korean Won;KRW;9.234019605670427E-4
Kuwaiti Dinar;KWD;3.541477
Kazakhstani Tenge;KZT;0.006652700430895407
Sri Lankan Rupee;LKR;0.007673919799864172
Lithuanian Litas;LTL;0.3711459278982229
Latvian Lat;LVL;1.840489
Libyan Dinar;LYD;0.7892659826361484
Mauritian Rupee;MUR;0.03184726560969838
Mexican Peso;MXN;0.07682686604774944
Malaysian Ringgit;MYR;0.32692238535649254
Norwegian Krone;NOK;0.17440900201599366
Nepalese Rupee;NPR;0.011331841534168165
New Zealand Dollar;NZD;0.815167993896022
Omani Rial;OMR;2.597063
Philippine Peso;PHP;0.024286723225587715
Pakistani Rupee;PKR;0.010416678602444232
Polish Zloty;PLN;0.3111993810866709
Qatari Riyal;QAR;0.27458240135489936
Romanian New Leu;RON;0.2825950020813122
Russian Ruble;RUB;0.03195499713843001
Saudi Arabian Riyal;SAR;0.2666314002201309
Swedish Krona;SEK;0.14775171388294311
Singapore Dollar;SGD;0.8166231814822527
Thai Baht;THB;0.032597074086629985
Turkish Lira;TRY;0.5569401707021623
Trinidadian Dollar;TTD;0.15679633723756214
Taiwan New Dollar;TWD;0.03434332472231705
US Dollar;USD;1.0
Venezuelan Bolivar Fuerte;VEF;0.23255813953488372
South African Rand;ZAR;0.11293473095333174""".split("\n").map { line => line.split(";") }

  val currencies = data.map { elem => Currency(elem(0), elem(1), elem(2).toDouble) }.toSet

}