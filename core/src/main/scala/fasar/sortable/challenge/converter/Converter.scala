package fasar.sortable.challenge.converter

import  fasar.sortable.challenge.model._

trait Converter {

  /** Get data you want in a good format
   * 
   *  This function helps to get a file of links.
   *  A link is a relation between a product and a listing
   *  
   *  @param link  the link
   *  @return      content of the data file
   */
  def convert(links: List[Link]): String
  
  /** Get a link element in a good format
   * 
   *  This function helps to get an element of a file.
   *  It is helpful when file doesn't require headers
   * 
   * @param link  the link
   * @return      a line of the data file
   */
  def convert(link: Link): String
  
}