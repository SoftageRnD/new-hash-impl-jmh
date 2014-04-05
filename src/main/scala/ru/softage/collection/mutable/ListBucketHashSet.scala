package ru.softage.collection.mutable

import scala.collection.generic.{MutableSetFactory, CanBuildFrom}
import scala.collection.{TraversableOnce, mutable}

/**
 * Mutable HashSet with buckets implemented using mutable linked list
 */
class ListBucketHashSet[A]
  extends mutable.Set[A]
  with mutable.SetLike[A, ListBucketHashSet[A]] {

  private val loadFactor = ListBucketHashSet.DefaultLoadFactor

  private[this] var table = new Array[AnyRef](ListBucketHashSet.DefaultInitialCapacity)
  private var collectionSize = 0
  private var threshold = calculateThreshold()
  private var containsNull = false

  override def size: Int = collectionSize

  private def calculateThreshold(): Int = (table.length * loadFactor).asInstanceOf[Int]


  private def resizeTable(newTableSize: Int) {
    val newTable = new Array[AnyRef](newTableSize)
    var index = 0
    while (index < table.length) {
      table(index) match {
        case null => Unit
        case bucket: ListBucket => bucket.foreach(putIntoTableWithoutCheck(newTable))
        case value => putIntoTableWithoutCheck(newTable)(value)
      }
      index += 1
    }
    table = newTable
    threshold = calculateThreshold()
  }

  /**
   *
   * @return if the same element already existed
   */
  private def putIntoTable(table: Array[AnyRef])(elem: AnyRef): Boolean = {
    val index = getIndex(table.length)(elem)
    table(index) match {
      case null =>
        table.update(index, elem)
        false
      case bucket: ListBucket => bucket.add(elem)
      case value =>
        val isSame = value == elem
        if (!isSame)
          table.update(index, ListBucket(value, elem))
        isSame
    }
  }

  /**
   * Previously existed elements replaced without any checks
   */
  private def putIntoTableWithoutCheck(table: Array[AnyRef])(elem: AnyRef) {
    val index = getIndex(table.length)(elem)
    table(index) match {
      case null => table.update(index, elem)
      case bucket: ListBucket => bucket.addWithoutCheck(elem)
      case value => table.update(index, ListBucket(value, elem))
    }
  }

  override def ++=(xs: TraversableOnce[A]): this.type = {
    val numOfElementsToAdd = xs.size
    if (numOfElementsToAdd > threshold) {
      val predictedCapacity: Int = (numOfElementsToAdd / loadFactor + 1).asInstanceOf[Int]
      var newCapacity: Int = table.length
      while (newCapacity < predictedCapacity)
        newCapacity <<= 1
      if (newCapacity > table.length)
        resizeTable(newCapacity)
    }
    super.++=(xs)
  }

  override def add(elem: A): Boolean = {
    val elemRef = elem.asInstanceOf[AnyRef]
    if (elemRef eq null) {
      if (!containsNull) {
        containsNull = true
        collectionSize += 1
        return true
      }
      return false
    }

    val expectedCollectionSize = collectionSize + 1
    if (expectedCollectionSize > threshold)
      resizeTable(table.length * 2)
    val newElementAdded = !putIntoTable(table)(elemRef)
    if (newElementAdded)
      collectionSize = expectedCollectionSize
    newElementAdded
  }

  override def remove(elem: A): Boolean = {
    val elemRef = elem.asInstanceOf[AnyRef]
    if (elemRef eq null) {
      if (containsNull) {
        containsNull = false
        collectionSize -= 1
        return true
      }
      return false
    }

    val index = getIndex(table.length)(elemRef)
    table(index) match {
      case null => false
      case bucket: ListBucket =>
        val wasRemoved = bucket.remove(elemRef)
        if (wasRemoved) {
          if (bucket.isOneElemRemained)
            table.update(index, bucket.getSingleValue)
          collectionSize -= 1
        }
        wasRemoved
      case value =>
        val sameElem = value == elemRef
        if (sameElem) {
          table.update(index, null)
          collectionSize -= 1
        }
        sameElem
    }
  }

  override def +=(elem: A): this.type = {
    add(elem)
    this
  }

  def -=(elem: A): this.type = {
    remove(elem)
    this
  }

  def contains(elem: A): Boolean = {
    val elemRef = elem.asInstanceOf[AnyRef]
    if (elemRef eq null)
      containsNull
    else
      getCell(elemRef) match {
        case null => false
        case bucket: ListBucket => bucket.contains(elemRef)
        case value => value == elemRef
      }
  }

  def iterator: Iterator[A] = new Iterator[A] {
    private var index = -1
    private var bucketIterator: Iterator[AnyRef] = null
    private var elemsVisited = 0

    def hasNext: Boolean = elemsVisited < collectionSize

    def next(): A =
      if (hasNext) {
        if (index == -1) {
          index = 0
          if (containsNull) {
            elemsVisited += 1
            return null.asInstanceOf[A]
          }
        }

        if (bucketIterator != null) {
          val elem = bucketIterator.next()
          if (!bucketIterator.hasNext) {
            bucketIterator = null
            index += 1
          }
          elemsVisited += 1
          elem.asInstanceOf[A]
        } else {
          while (index < table.length) {
            table(index) match {
              case null => index += 1
              case bucket: ListBucket =>
                bucketIterator = bucket.iterator
                elemsVisited += 1
                return bucketIterator.next().asInstanceOf[A]
              case value =>
                elemsVisited += 1
                index += 1
                return value.asInstanceOf[A]
            }
          }
          throw new IllegalStateException("next element supposed to be but not found")
        }
      } else throw new IllegalStateException("no next element for the iterator")
  }

  override def foreach[U](f: A => U) {
    var index = 0
    if (containsNull)
      f(null.asInstanceOf[A])
    while (index < table.length) {
      table(index) match {
        case null => Unit
        case bucket: ListBucket => bucket.foreach((el: AnyRef) => f(el.asInstanceOf[A]))
        case value => f(value.asInstanceOf[A])
      }
      index += 1
    }
  }

  override def empty = new ListBucketHashSet[A]

  private def getIndex(tableSize: Int)(elem: AnyRef): Int = elem.## & tableSize - 1

  private def getCell(elem: AnyRef): Any = table(getIndex(table.length)(elem))
}

object ListBucketHashSet extends MutableSetFactory[ListBucketHashSet] {
  private val DefaultInitialCapacity = 16
  private val DefaultLoadFactor = 0.75f

  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, ListBucketHashSet[A]] = setCanBuildFrom[A]

  override def empty[A]: ListBucketHashSet[A] = new ListBucketHashSet[A]
}
