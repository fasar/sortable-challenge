package fasar.sortable.challenge.model


import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import java.io._
import scala.util.parsing.json._
import java.net._


@RunWith(classOf[JUnitRunner])
class ModelJsonImportTest extends FunSuite {

	trait TestSets {
		
		val listProd = RawJsonData.products.split("\n")
		val prodToRead = listProd.size	
		
		val listItems = RawJsonData.listings.split("\n")
		val itemToRead = listItems.size
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
