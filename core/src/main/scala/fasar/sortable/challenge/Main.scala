package fasar.sortable.challenge

import fasar.sortable.challenge.model._
import fasar.sortable.challenge.jsonTools.JsonTools
import fasar.sortable.challenge.helper._
import io.Source
import org.apache.commons.logging.{Log, LogFactory}
import java.net.URI
import java.util.Properties
import scala.annotation.tailrec
import java.util.Calendar
import scala.collection.parallel.ParSeq
import java.io.PrintWriter
import scala.util.parsing.json.JSON
import scala.util.matching.Regex
import scala.collection.parallel.immutable.ParHashSet
import scala.collection.immutable.HashSet

/**The main object used to execute sortable challenge on the command-line.
 *
 * @author Fabien Sartor
 */
object Main {
  private val log: Log = LogFactory.getLog(this.getClass)

  def main(argsa: Array[String]): Unit = {
    val dateStartApp = getDate

    val args = argsa.toList
    log.debug("Application starting with args " + args)

    FileHelper.loadPrivateProperties()

    // Parse arguments
    val nbArgs = args.size
    if (nbArgs == 0) {
      usage(args)
      throw new ChallengeApplicationException("no arguments")
    }

    args foreach {
      case "-h" => usage(args); System.exit(0)
      case "--help" => usage(args); System.exit(0)
      case _ =>
    }

    // load properties from arguments
    val prop = loadArgsProperties(args)


    // open output file
    val outFic = FileHelper.getOutFic(prop)

    // open input file
    val file1String = prop.getProperty("file1")
    val file2String = prop.getProperty("file2")
    log.trace("file 1 property is " + file1String)
    log.trace("file 2 property  is " + file2String)

    val file1URI = Loader.getFileURI(file1String)
    val file2URI = Loader.getFileURI(file2String)
    log.trace("file 1 uri is " + file1URI)
    log.trace("file 2 uri is " + file2URI)

    FileHelper.testFile(file1URI)
    FileHelper.testFile(file2URI)

    val dateEndInit = getDate

    println("Load data files")
    // Load data
    // get parallel collection of products to use multi-processors
    // and order products by the most informative in first 
    val listProds: ParSeq[Product] = loadProductData(file1URI).sortWith(_.isGreaterThan(_)).par
    val listItems: Seq[Item] = loadItemData(file2URI)

    val dateEndLoadData = getDate

    println("Start processing")
    
    // Process record linkage of product with family
    val listLinkWithFamily =
      for (prod <- listProds
           if prod.hasFamily)
      yield {
        log.trace("Product processed : " + prod)
        val modelReg = RegexHelper.makeAcronymRegex(prod.model.toLowerCase)
        val familyReg = RegexHelper.makeAcronymRegex(prod.family.toLowerCase)

        // get items with max information - c.f. all items have a family
        val itemsProd =
          for (item <- listItems
               if item.manufacturer.toLowerCase == prod.manufacturer.toLowerCase &&
                 isContained(item.title.toLowerCase, familyReg) &&
                 isContained(item.title.toLowerCase, modelReg)
          )
          yield {
            item
          }
        Link(prod, itemsProd.toList)
      }
    
    // get the items already linked with a product
    val alreadyLinked = listLinkWithFamily.foldLeft(HashSet.empty[Item]){ (set, link) => set ++ link.items  }
    val listItemsMinus = listItems.toSet.par &~ alreadyLinked
    
    // Process record linkage of product without family
    val listLinkWithoutFamily =
      for (prod <- listProds
           if !prod.hasFamily)
      yield {
        log.trace("Product processed : " + prod)
        val modelReg = RegexHelper.makeAcronymRegex(prod.model.toLowerCase)
        val familyReg = RegexHelper.makeAcronymRegex(prod.family.toLowerCase)

        // get items with max information - c.f. all items have a family
        val itemsProd =
          for (item <- listItemsMinus 
                 if item.manufacturer.toLowerCase == prod.manufacturer.toLowerCase &&
                 isContained(item.title.toLowerCase, familyReg) &&
                 isContained(item.title.toLowerCase, modelReg)
          )
          yield {
            item
          }
        Link(prod, itemsProd.toList)
      }    

    // process result
    val links =  listLinkWithFamily ++ listLinkWithoutFamily
    
        
        
    val dateEndProcess = getDate

    val pout = new PrintWriter(outFic)
    links.toList.foreach {x => pout.println(JsonTools.getJson(x)) }

    val dateEndWriting = getDate

    // close output file
    pout.close()

    log.debug("It takes " + (dateEndInit - dateStartApp) + " ms to init properties and file")
    log.debug("It takes " + (dateEndLoadData - dateEndInit) + " ms to load data")
    log.debug("It takes " + (dateEndProcess - dateEndInit) + " ms to process data")
    log.debug("It takes " + (dateEndWriting - dateEndProcess) + " ms to write data")

    println("Done with : " + outFic.getCanonicalPath())
  }

  /** get current date. */
  private def getDate = Calendar.getInstance.getTimeInMillis

  /** Prints usage information for scalap. */
  private def usage(args: Seq[String]): Unit = {
    System.out.println("Usage :  [OPTION] file_products file_listing")
    System.out.println("Record Linkage of products and listing\n")
    System.out.println("  -h, -help             print this message")
    System.out.println("  -o, --output [FILE]   output file")
    System.out.println("                         if the output is not set, it uses 'outputDefault' in properties file")
    System.out.println("   ")
    System.out.println(" file_product   a json file of products")
    System.out.println(" file_listing   a json listings file")
    System.out.println("\n\n" +
      " properties file is conf/diffTools.properties file")
  }

  /** load arguments as a properties */
  private def loadArgsProperties(args: Seq[String]): Properties = {
    @tailrec
    def parseCmdRec(args: List[String], prop: Properties): Properties = {
      args match {
        case file1 :: file2 :: Nil =>
          prop.setProperty("file1", file1)
          prop.setProperty("file2", file2)
          prop
        case "-o" :: path :: tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case "--output" :: path :: tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case x :: tail =>
          parseCmdRec(tail, prop)
        case Nil => prop
      }
    }
    parseCmdRec(args.toList, new Properties())
  }

  /** Load products data
    *
    * @param file  uri of the file to load product
    * @return      a list of Product
    */
  private def loadProductData(file: URI): List[Product] = {
    loadData(file) {
      map => Product.getProduct(map)
    }
  }
  /** Load listing data
    *
    * @param file  uri of the file to load items
    * @return      a list of item
    */
  private def loadItemData(file: URI) = {
    loadData(file) {
      map => Item.getItem(map)
    }
  }

  /** Generic function to load data of a type
    *
    * @param file    the file of the data
    * @param builder a function takes two parameter and return a type B.
    *                First param is a map of Json data which link data name with data.
    *                Second param is the json source.
    * @tparam B      Type
    * @return        A list of [B]
    */
  private def loadData[B](file: URI)(builder: Map[String, String] => B): List[B] = {
    val ficIn = Source.fromURI(file)
    val data =
      for (line <- ficIn getLines)
      yield {
        val productOpt = JSON.parseFull(line)
        val product = productOpt.get
        builder(product.asInstanceOf[Map[String, String]])
      }
    val res = data.toList
    ficIn.close()
    res
  }



  /** Find if src contains the regex pattern cont
    *
    * @param src   the text you want to know if a pattern is inside
    * @param cont  the pattern
    * @return      true if cont is found inside src, false otherwise
    */
  private def isContained(src: String, cont: Regex): Boolean = {
    cont.findFirstIn(src).isDefined
  }
}
