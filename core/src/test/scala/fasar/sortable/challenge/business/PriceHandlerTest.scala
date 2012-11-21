package fasar.sortable.challenge.business

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PriceFilterTest extends FunSuite {

  test("test average") {
    val list = List(60.0, 56.0, 61.0, 68.0, 51.0, 53.0, 69.0, 54.0)
    val average = PriceFilter.average(list)
    println("Average : " + average)
    assert(average === 59.0)
  }

  test("test standard deviation") {
    val list = List(60.0, 56.0, 61.0, 68.0, 51.0, 53.0, 69.0, 54.0)
    val average = PriceFilter.average(list)
    val standardDeviation = PriceFilter.standardDeviation(list, average)
    println("StandardDeviation : " + standardDeviation)
    val res = standardDeviation - 6.32
    assert(res > 0.00 && res < 0.01)
    
  }
  
}
