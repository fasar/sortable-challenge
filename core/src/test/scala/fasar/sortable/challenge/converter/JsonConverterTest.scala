package fasar.sortable.challenge.converter

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fasar.sortable.challenge.model._
import fasar.sortable.challenge.converter.impl.JsonConverter



@RunWith(classOf[JUnitRunner])
class JsonTest extends FunSuite {

  trait TestSet {
    val prod1 = Product("Prod1", "Man1", "Model1", "Date1", "Family1")
    val prod2 = Product("Prod2", "Man2", "Model2", "Date2")
    val item1 = Item("Title1", "Man1", "Currency1", "Price1")
    val item2 = Item("Title2", "Man2", "Currency2", "Price2")
    val item3 = Item("Title3", "Man3", "Currency3", "Price3")
    val link1 = Link(prod1, item1 :: item3 :: Nil)
    val link2 = Link(prod2, item1 :: item2 :: item3 :: Nil)
    val linkEmptyItems = Link(prod2, Nil)

    val resLink1 = """{"product-name":"Prod1","listings":[{"title":"Title1","manufacturer":"Man1","currency":"Currency1","price":"Price1"},{"title":"Title3","manufacturer":"Man3","currency":"Currency3","price":"Price3"}]}"""
    val resLink2 = """{"product-name":"Prod2","listings":[{"title":"Title1","manufacturer":"Man1","currency":"Currency1","price":"Price1"},{"title":"Title2","manufacturer":"Man2","currency":"Currency2","price":"Price2"},{"title":"Title3","manufacturer":"Man3","currency":"Currency3","price":"Price3"}]}"""
    val resEmptyItems = """{"product-name":"Prod2","listings":[]}"""
  }

  new TestSet {
    test("test json a product with a family") {
      val res = JsonConverter.convert(link1)
      println(res)
      assert(res === resLink1)
    }

    test("test json a product with no family") {
      val res = JsonConverter.convert(link2)
      println(res)
      assert(res === resLink2)
    }

    test("test json a product without items") {
      val res = JsonConverter.convert(linkEmptyItems)
      println(res)
      assert(res === resEmptyItems)
    }
  }
}
