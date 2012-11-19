package fasar.sortable.challenge.helper


import java.lang.reflect.InvocationTargetException
import org.apache.commons.logging.{LogFactory, Log}
import java.io.{File, InterruptedIOException}
import java.net.{URI, URL}

/** Helper to handle resources
  *
  * @author Fabien Sartor
  */
object Loader {

  private val log: Log = LogFactory.getLog(this.getClass)

  val TSTR: String = "Caught Exception while in Loader.getResource. This may be innocuous."

  /** Get a URI of file in all classpath
    *
    * @param resource  the name of the resource
    * @return
    */
  def getResource(resource: String): URL = {
    var classLoader: ClassLoader = null
    var url: URL = null

    try {
      classLoader = getTCL
      log.debug("Trying to find [" + resource + "] using context classloader " + classLoader + ".")
      url = classLoader.getResource(resource)
      if (url != null) {
        return url
      }

      // We could not find resource. Let us now try with the
      // classloader that loaded this class.
      classLoader = this.getClass.getClassLoader
      if (classLoader != null) {
        log.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.")
        url = classLoader.getResource(resource)
        if (url != null) {
          return url
        }
      }
    } catch {
      case t: IllegalAccessException =>
        log.warn(TSTR, t)
      case t: InvocationTargetException =>
        if (t.getTargetException().isInstanceOf[InterruptedException]
          || t.getTargetException().isInstanceOf[InterruptedIOException]) {
          Thread.currentThread().interrupt()
        }
        log.warn(TSTR, t)
      case t: Throwable =>
        //
        //  can't be InterruptedException or InterruptedIOException
        //    since not declared, must be error or RuntimeError.
        log.warn(TSTR, t)
    }

    // Last ditch attempt: get the resource from the class path. It
    // may be the case that clazz was loaded by the Extentsion class
    // loader which the parent of the system class loader. Hence the
    // code below.
    log.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().")

    ClassLoader.getSystemResource(resource)
  }


  /** Get a URI of file in classpath or in the har disk
    *
    * This function looks in all classpath to get a resource.
    * If it don't found, it looks in hard disk.
    *
    * @param filePath the
    * @return
    */
  def getFileURI(filePath: String): URI = {
    val resUri = getResource(filePath)
    if (resUri == null) {
      try {
        // fix for windows
        val fic = new File(filePath)
        if (fic != null && fic.exists()) {
          fic.toURI
        } else {
          null
        }
      } catch {
        case e: Exception =>
          log.error("Can't get file " + filePath + ".\n" + e.toString + "\n" + e.getStackTraceString)
          null
      }
    } else {
      resUri.toURI
    }
  }

  /**
   * Get the Thread Context Loader which is a JDK 1.2 feature. If we
   * are running under JDK 1.1 or anything else goes wrong the method
   * returns <code>null<code>.
   *
   **/
  private def getTCL: ClassLoader = {
    Thread.currentThread.getContextClassLoader
  }

}
