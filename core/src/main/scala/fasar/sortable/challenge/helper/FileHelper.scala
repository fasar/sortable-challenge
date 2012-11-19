package fasar.sortable.challenge.helper

import fasar.sortable.challenge.ChallengeApplicationException
import java.util.Properties
import java.io.File
import java.net.URL
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import java.net.URI
import scala.io.Source
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

/** Helper to handle files
  *
  * @author Fabien Sartor
  */
object FileHelper {
  private val log: Log = LogFactory.getLog(this.getClass)


  /** load property file
    *
    */
  def loadPrivateProperties() {
    try {
      // set up new properties object from file "myProperties.txt"
      val fileUrl = Loader.getResource("challenge.properties")

      val buff = Source.fromURL(fileUrl)
      val p: Properties = new Properties(System.getProperties())
      p.load(buff.reader)

      // set the system properties
      System.setProperties(p)
      log.debug("System properties file is loaded : " + fileUrl.getFile)

      // display new properties
      val out = new ByteArrayOutputStream()
      val pout = new PrintWriter(out)
      System.getProperties().list(pout)
      pout.flush
      val str = new String(out.toByteArray)
      pout.close()
      out.close()
      log.trace("New System properties is " + str)
    } catch {
      case e: java.io.FileNotFoundException =>
        log.warn("Can't find diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
      case e: java.io.IOException =>
        log.error("I/O failed. " + e.toString + "\n" + e.getStackTraceString)
      case e =>
        log.error("Can't load diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
    }
  }

  /** get the output file from the property object
    *
    * @param prop  this property need a a value for output or outputDefault
    * @return      get the output file with the value of prop or output.txt
    */
  def getOutFic(prop: Properties): File = {
    val outputParam = prop.getProperty("output")
    val outputDefault = System.getProperty("outputDefault")
    val fileOutput: URL =
      if (outputParam != null) {
        new File(outputParam).toURI.toURL
      } else if (outputDefault != null) {
        new File(outputDefault).toURI.toURL
      } else {
        new File("output.txt").toURI.toURL
      }
    try {
      val outFic = new File(fileOutput.toURI)
      if (outFic.exists() && !outFic.isFile) {
        log.error("output params " + outFic.getCanonicalFile + " is not a file ")
        null
      }
      if (outFic.exists) {
        outFic.delete
      }
      outFic.createNewFile()
      outFic
    } catch {
      case e: Exception =>
        log.error("Can't load file " + fileOutput.toString + "\n" + e.toString + "\n" + e.getStackTraceString)
        System.err.println("Can't create or modify file " + fileOutput.toString)
        throw new ChallengeApplicationException("Can't load file ", e)
        null
    }
  }

  /** Test if a file is accessible
    *
    * It throw an exception if the file is not accessible.
    *
    * @param fileURI uri of the file
    */
  def testFile(fileURI: URI) {
    if (fileURI == null) {
      System.err.println("Can't get file")
      throw new ChallengeApplicationException("Can't get file")
    } else {
      val file = new File(fileURI)
      if (file == null || !(file.isFile && file.canRead)) {
        throw new ChallengeApplicationException("Can't get file : " + fileURI.getPath)
      }
    }
  }
}
