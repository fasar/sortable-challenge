package fasar.sortable.challenge.model

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import java.io._
import scala.util.parsing.json._
import java.net._

@RunWith(classOf[JUnitRunner])
class ModelTest extends FunSuite {

	trait TestSets {
		val prodUrl = Thread.currentThread().getClass().getResource("/products.txt")
		val prodFic = new File(prodUrl.toURI)
		val prodIn = new BufferedReader(new InputStreamReader(new FileInputStream(prodFic)))
		val prodToRead = 500
		val listProd = 
			for(pd <- 0 until prodToRead) 
			yield { prodIn.readLine() }

		val itemUrl = Thread.currentThread().getClass().getResource("/listings.txt")
		val itemFic = new File(itemUrl.toURI)
		val itemIn = new BufferedReader(new InputStreamReader(new FileInputStream(itemFic)))
		val itemToRead = 500
		val listItems = 
			for(pd <- 0 until itemToRead) 
			yield { itemIn.readLine() }

	}

	new TestSets {
		test("load some products") {
			val products = 
					for(jsonProduct <- listProd)
					yield {
						val productOpt = JSON.parseFull(jsonProduct)
						val product = productOpt.get
						Product.getProduct(product.asInstanceOf[Map[String, String]])
					}
			assert(products.size === prodToRead) 
			println(products(0))
		}



		test("load some items in listing") {
			val items = 
					for(jsonItem <- listItems)
					yield {
						val productOpt = JSON.parseFull(jsonItem)
						val product = productOpt.get
						Item.getItem(product.asInstanceOf[Map[String, String]])
					}
			assert(items.size === prodToRead)
			println(items(0))
		}

	}
}
