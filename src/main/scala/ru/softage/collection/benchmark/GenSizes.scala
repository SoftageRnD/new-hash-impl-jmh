package ru.softage.collection.benchmark

/**
 * @author Misha Sokolov
 */
object GenSizes extends App {
  val minSize = 1000
  val maxSize = 57000
  val numOfSizes = 20

  val sizes = (0 until numOfSizes).map(i => {
    Math.round(minSize + 1.0 * (maxSize - minSize) * i / (numOfSizes - 1))
  })
  println(sizes.size)

  println("@Param({" + sizes.map("\"" + _ + "\"").mkString(", ") + "})")
}
