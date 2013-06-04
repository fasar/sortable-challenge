package fasar.sortable.challenge

import fasar.sortable.challenge.model._
import fasar.sortable.challenge.converter.Converter
import fasar.sortable.challenge.converter.impl._
import fasar.sortable.challenge.helper._
import scala.io.Source
import org.apache.commons.logging.{ Log, LogFactory }
import java.net.URI
import java.util.Properties
import scala.annotation.tailrec
import java.util.Calendar
import scala.collection.parallel.ParSeq
import java.io.PrintWriter
import scala.util.parsing.json.JSON
import scala.util.matching.Regex
import scala.collection.immutable.HashSet
import fasar.sortable.challenge.business.PriceHandler
import java.io.File
import fasar.sortable.challenge.json.FasarJson

/** The main object used to execute sortable challenge on the command-line.
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
      case "-h"     => usage(args); System.exit(0)
      case "--help" => usage(args); System.exit(0)
      case _        =>
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
    // and order products by the most informative in first 
    val listProds: Seq[Product] = loadProductData(file1URI).sortWith(_.isMoreInformativeThan(_))
    // get parallel collection of products to use multi-processors
    val listItems: ParSeq[Item] = loadItemData(file2URI).par

    val dateEndLoadData = getDate

    println("Start processing")

    // Process record linkage of product with family
    val links = getLinks(listProds, listItems)
    val fileLinks = scala.util.Marshal.dump(links)
    val outFileLinks = new java.io.BufferedOutputStream(new java.io.FileOutputStream("Links.obj"))
    outFileLinks.write(fileLinks )
    outFileLinks.close()

    val dateEndRecordLinkage = getDate

    // filter items with price too below the average price 
    val filtredLinks = filterLinks(links)
    val fileFiltredLinks = scala.util.Marshal.dump(links)
    val outFiltredFileLinks = new java.io.BufferedOutputStream(new java.io.FileOutputStream("FiltredLinks.obj"))
    outFiltredFileLinks.write(fileFiltredLinks )
    outFiltredFileLinks.close()

    val dateEndProcess = getDate

    //TODO: Suppr this § till END TO SUPPR
    //Just a little addon: get the rejected term.
    println("Processing bad terms : ")
    val spliter = Array(' ', '/', '.', ',', '\\', '(', ')')
    val acceptedTerm:Set[String] =
      (for(link <- filtredLinks;
          item <- link.items;
          term <- item.title.split(spliter))
      yield {
        term
      }).toSet
    val allTerm:Set[String] =
      (for(link <- links;
          item <- link.items;
          term <- item.title.split(spliter))
      yield {
        term
      }).toSet

    val falseTerms = allTerm diff acceptedTerm

    println("Les mauvais termes sont : ")
    println(falseTerms.toList.sorted.mkString("  ", "\n  ", "\n"))
    println("Il y a " + falseTerms.size + " mauvais  termes sont : ")
    //END TO SUPPR

    //Write output
    val converter = JsonConverter
    val pout = new PrintWriter(outFic)
    pout.write(converter convert filtredLinks)

    // close output file
    pout.close()

    // TODO: suppr these line after.
    // it is for debug
    val out2 = new PrintWriter(new File("/tmp/jsonNonFiltred.json"))
    out2.write(converter convert links)
    out2.flush()
    out2.close()

    val dateEndWriting = getDate

    log.debug("It takes " + (dateEndInit - dateStartApp) + " ms to init properties and file")
    log.debug("It takes " + (dateEndLoadData - dateEndInit) + " ms to load data")
    log.debug("It takes " + (dateEndProcess - dateEndLoadData) + " ms to process data")
    log.debug("  and in this time :")
    log.debug("   It takes " + (dateEndRecordLinkage - dateEndLoadData) + " ms to do the record linkage")
    log.debug("   It takes " + (dateEndProcess - dateEndRecordLinkage) + " ms to filter records")
    log.debug("It takes " + (dateEndWriting - dateEndProcess) + " ms to write data")

    println("Done in "+ ((dateEndWriting -dateStartApp)/1000) +" sec with : " + outFic.getCanonicalPath())
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
    *              First param is a map of Json data which link data name with data.
    *              Second param is the json source.
    * @tparam B      Type
    * @return        A list of [B]
    */
  private def loadData[B](file: URI)(builder: List[String] => B): List[B] = {
    val ficIn = Source.fromURI(file)
    val data =
    // This is the old way to get data. It uses JSON object.
    // JSON parsing is very processing intensive
//      for (line <- ficIn getLines) yield {
//        val productOpt = JSON.parseFull(line)
//        val product = productOpt.get
//        builder(product.asInstanceOf[Map[String, String]])
//      }
    //New way to do the job
    for (line <- ficIn getLines) yield {
      val values = FasarJson.parseLine(line)
      builder(values)
    }
    val res = data.toList
    ficIn.close()
    res
  }

  /** get a list of links between products and items
    *
    * @param products       the reference products
    * @param listItems      the listing you want to link with products
    * @return
    */
  private def getLinks(products: Seq[Product], listItems: ParSeq[Item]): List[Link] = {
    @tailrec
    def loop_getLinks(products: Seq[Product], alreadyLinked: Set[Item], acc: List[Link]): List[Link] = {
      products match {
        case Nil => acc
        case prod :: xs =>
          log.trace("Product processed : " + prod)
          val link = getLink(prod, listItems, alreadyLinked)

          loop_getLinks(xs, alreadyLinked ++ link.items, link :: acc)
      }
    }

    loop_getLinks(products, HashSet.empty[Item], Nil)
  }

  /** get link between a product and items
    *
    * @param product
    * @param listItems
    * @param alreadyLinked
    * @return
    */
  private def getLink(product: Product, listItems: ParSeq[Item], alreadyLinked: Set[Item]): Link = {
    val modelReg = RegexHelper.makeAcronymRegex(product.model.toLowerCase)
    lazy val familyReg = RegexHelper.makeAcronymRegex(product.family.toLowerCase)

    val itemsProd =
      for (
        item <- listItems if (isContained(item.title.toLowerCase, modelReg) &&
          (!product.hasFamily || isContained(item.title.toLowerCase, familyReg)))
      ) yield {
        item
      }
    Link(product, itemsProd.toList)
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

  private def filterLinks(links: List[Link]): List[Link] = {
    for (link <- links) yield {
      val filtredLink = filterLink(link)
      val nbFiltred = link.items.size - filtredLink.items.size
      log.debug("There is : [ " + nbFiltred + " ] filtred item.")
      filtredLink
    }
  }

  private def filterLink(link: Link): Link = {
    val average = PriceHandler.averageUsdPrice(link).value
    val standardDeviation = PriceHandler.standardDeviationUsdPrice(link, average)

    val nbItems = link.items.size
    val expandedDeviation = getExpendedDeviation(standardDeviation, nbItems)
    val minPrice = average - expandedDeviation
    val maxPrice = average + expandedDeviation


    log.debug("Normal min price is : " + (average - standardDeviation) + " and expanded is : " + minPrice)

    def isGoodItem(item: Item): Boolean = {
      val price = item.getPrice
      if (price.isDefined) {
        val usdPrice = price.get.getUsdPrice
        minPrice <= usdPrice
      } else {
        false
      }
    }

    val items =
      for (item <- link.items if isGoodItem(item)) yield {
        item
      }

    Link(link.product, items)
  }

  private def getExpendedDeviation(deviation: Double, nbItems: Int): Double = {
    if (nbItems < 25) deviation * (1 / math.sqrt(nbItems) + 0.85)
    else deviation
  }
}
