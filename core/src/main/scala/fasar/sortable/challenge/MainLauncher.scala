package fasar.sortable.challenge

object MainLauncher {

  def main(argsa: Array[String]): Unit = {

    println("Type enter to start the application.")
    Console.in.read()

    Main.main(Array("-o", "/tmp/dede",
      "/home/fabien/work/sandbox/Projects/sortable/challenge_data/productsExtrait.txt",
      "/home/fabien/work/sandbox/Projects/sortable/challenge_data/listingsExtrait.txt"))
  }
}
