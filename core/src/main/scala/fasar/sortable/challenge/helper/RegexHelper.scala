package fasar.sortable.challenge.helper

import util.matching.Regex

/** Helper to handle regex */
object RegexHelper {

  /** Delimitiers in a text */
  private object Delimiters {
    /** Delimitier in a text */
    type Delimiter = Char
    val spaceChars: Set[Delimiter] = Set('\n', '\t', ' ')
    val puctChars: Set[Delimiter] = Set('!', '.', ',', ':', ';', '?')
    val leftQuoteChars: Set[Delimiter] = Set('"', '(', '<')
    val rightQuoteChars: Set[Delimiter] = Set('"', ')', '>')
    val sepChars: Set[Delimiter] = Set('/', '\\')

    val delimiters = sepChars ++ spaceChars ++ puctChars ++ leftQuoteChars ++ rightQuoteChars
    val escapedDelimiters = escapeListChar(delimiters)

    /** Make a list a delimiters regex compatible */
    private def escapeListChar(chars: Set[Char]): Set[String] = {
      for (char <- chars)
      yield {
        escapeRegexChar(char)
      }
    }
  }

  /** Make regex from a acronym
    *
    * It produces regex expression of the acronym. This regex can be found in a text.
    *
    * @param acronym   acronym you want to regexify to search it in a text
    * @return           regex of the acronym
    */
  def makeAcronymRegex(acronym: String): Regex = {
    val delimiters = Delimiters.escapedDelimiters
    val delim = delimiters.mkString("(", "|", ")")
    val escapedAcronyme = regexEscape(acronym)
    val patternR = escapedAcronyme.replaceAll("-", ".?")
      .replaceAll("_", ".?")
      .replaceAll(" ", ".?")
    (delim + patternR + delim).r
  }

  /** escape a plain text to be regex compatible
    *
    * @param txt  source
    * @return     compatible regex text
    */
  def regexEscape(txt: String): String = {
    txt.map {
      escapeRegexChar
    }.mkString
  }

  /** escape a character
    *
    * @param char  source character
    * @return      regex compatible character
    */
  def escapeRegexChar(char: Char): String = {
    char match {
      case '.' => """\."""
      case '?' => """\?"""
      case '(' => """\("""
      case ')' => """\)"""
      case '\\' => """\\"""
      case ']' => """\]"""
      case '[' => """\["""
      case '*' => """\*"""
      case '|' => """\|"""
      case '^' => """\^"""
      case '$' => """\$"""
      case '+' => """\+"""
      case _ => char.toString
    }
  }
}
