package fasar.sortable.challenge.model

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ModelTest extends FunSuite {

  trait TestSet {
    val prod1a = Product("Prod1a", "Man1", "ZZZZ12345", "Date1")
    val prod1b = Product("Prod1b", "Man1", "SD12345", "Date1", "Family1")
    val prod1c = Product("Prod1c", "Man1", "DS12345", "Date1")

    val prod2 = Product("Prod2", "Man1", "SD12345", "Date1", "Family1")
    val prod3 = Product("Prod3", "Man1", "ADZSS", "Date1", "Family1")

    val listProds = prod3 :: prod2 :: prod1b :: prod1c :: prod1a :: Nil
    val listSorted = prod1a :: prod2 :: prod1b :: prod1c :: prod3 :: Nil

  }

  new TestSet {
    test("sort list") {
      println(listProds.sortWith(_.isMoreInformativeThan(_)).toString)
      assert(listProds.sortWith(_.isMoreInformativeThan(_)) === listSorted)
      assert(listProds.sortWith(!_.isMoreInformativeThan(_)) === listSorted.reverse)
    }
  }

  test("Usd is less than Euro") {
    val euroCur = Currency.getWithSymbol("EUR")
    val euroVal = 1
    val euroPrice = Price(euroCur.get, euroVal)
    val usdVal = euroPrice.getUsdPrice
    assert(euroVal < usdVal)
  }

  test("Euro is less than GBP (british pound)") {
    val euroCur = Currency.getWithSymbol("EUR")
    val euroVal = 1
    val euroPrice = Price(euroCur.get, euroVal)
    val gbpPrice = euroPrice.convertTo(Currency.getWithSymbol("GBP").get)
    assert(gbpPrice.value < euroVal)
  }
}
