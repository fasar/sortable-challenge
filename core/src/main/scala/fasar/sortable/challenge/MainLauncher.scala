package fasar.sortable.challenge

object MainLauncher {

  def main(argsa: Array[String]): Unit = {
    Main.main(Array("-o", "/tmp/dede",
      "/home/fabien/work/s2ih/sandbox/fabien/Projects/sortable/challenge_data/products.txt",
      "/home/fabien/work/s2ih/sandbox/fabien/Projects/sortable/challenge_data/listings.txt"))
  }
}