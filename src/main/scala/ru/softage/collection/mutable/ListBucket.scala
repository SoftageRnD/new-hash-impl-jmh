package ru.softage.collection.mutable

import ru.softage.collection.mutable.ListBucket.Node

/**
 * such style much imperative wow
 * {{{
 * ░░░░░░░░░▄░░░░░░░░░░░░░░▄
 * ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌
 * ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐
 * ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐
 * ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐
 * ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌
 * ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒
 * ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒
 * ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀ ▌
 * ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒ ▌
 * ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒ ▐
 * ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒ ▒▌
 * ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒ ▐
 * ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒ ▌
 * ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒
 * ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒
 * ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀
 * ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀
 * ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒
 * }}}
 */
class ListBucket(var head: Node) {
  def add(elem: AnyRef): Boolean =
    if (contains(elem)) true
    else {
      head = new Node(elem, head)
      false
    }

  def addWithoutCheck(elem: AnyRef): Unit = {
    head = new Node(elem, head)
  }

  def contains(elem: AnyRef): Boolean = {
    var node = head
    while (node != null) {
      if (elem == node.data) return true
      node = node.tail
    }
    false
  }

  def remove(elem: AnyRef): Boolean = {
    if (head.data == elem) {
      head = head.tail
      return true
    }

    var prevNode = head
    var node = head.tail
    while (node != null) {
      if (node.data == elem) {
        prevNode.tail = node.tail
        return true
      }
      prevNode = node
      node = node.tail
    }
    false
  }

  def getSingleValue: AnyRef = head.data

  def iterator: Iterator[AnyRef] = new Iterator[AnyRef] {
    var node = head

    def hasNext: Boolean = node != null

    def next(): AnyRef = {
      val data = node.data
      node = node.tail
      data
    }
  }

  def foreach[U](f: (AnyRef) => U): Unit = {
    var node = head
    while (node != null) {
      f(node.data)
      node = node.tail
    }
  }

  def isOneElemRemained: Boolean = head.tail == null
}

object ListBucket {
  def apply(elem1: AnyRef, elem2: AnyRef): ListBucket = new ListBucket(new Node(elem1, new Node(elem2, null)))

  class Node(val data: AnyRef, var tail: Node)

}
