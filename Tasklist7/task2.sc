
// 2. Dla drzew binarnych, zdefiniowanych poniżej, napisz funkcję breadthBT[A] : BT[A] => List[A]
//	  obchodzącą drzewo binarne wszerz i zwracającą listę wartości, przechowywanych w węzłach
//    drzewa. Wykorzystaj kolejkę z zadania 1.

sealed trait BT[+A]
case object Empty extends BT[Nothing]
case class Node[+A](elem: A, left: BT[A], right: BT[A]) extends BT[A]

class UnderflowException(msg: String) extends Exception(msg)

class MyQueue[+T] private (private val in: List[T], private val out: List[T]) {
	def this() = this(Nil, Nil)

	def enqueue[E >: T] (new_element: E): MyQueue[E] =
		in match {
			case _ :: _  => new MyQueue[E](in, new_element :: out)
			case   Nil   => new MyQueue[E](new_element :: in, out)
		}

	def dequeue: MyQueue[T] =
		in match {
			case _ :: Nil  => new MyQueue[T](out.reverse, Nil)
			case _ :: tail => new MyQueue[T](tail, out)
			case Nil => this
		}

	def first: T =
		in match {
			case head :: _ => head
			case Nil => throw new UnderflowException("Empty Queue.")
		}

	def isEmpty: Boolean = in == Nil

	override def toString: String = {s"($in, $out)"}
}

object MyQueue {

	def apply[T](xs: T*): MyQueue[T] = new MyQueue[T](xs.toList, Nil)
	def empty[T]: MyQueue[T] = new MyQueue[T](Nil, Nil)
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ----------------------------
// -      My Extra Tests      -
// ----------------------------

// Extra Test Trees:
val tree_empty = Empty
val tree_root = Node(5, Empty, Empty)
val tree_zero = Node(4, Node(-2, Empty, Empty), Node(-2, Empty, Empty))
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)
val tt = Node(1,
			Node(2,
				Node(4,
					Empty,
					Empty
				),
				Empty
			),
			Node(3,
				Node(5,
					Empty,
					Node(6,
						Empty,
						Empty
					)
				),
				Empty
			)
		)
val tree_big = Node(1,
				Node(2,
					Node(3,
						Empty,
						Empty),
					Empty),
				Node(4,
					Empty,
					Empty)
				)

// -------------------------------------------------------------------------

def breadthBT[A](tree: BT[A]): List[A] = {
	def search(visited: List[A]) (queue: MyQueue[BT[A]]): List[A] = {
		queue.first match {
			case Node(e, l, r) => e :: search(e :: visited)(queue.dequeue.enqueue(l).enqueue(r))
			case Empty => if (!queue.dequeue.isEmpty) search(visited)(queue.dequeue) else Nil
		}
	}
	search (Nil) (MyQueue(tree))
}

// ----------
// Tree Definitions
//
// t:                   zero:                  root:             empty:
//        [1]								        [4]                [5]                 [ ]
//     [2]   [ ]                [-2]   [-2]
//   [3] [ ]
//
//  tt:                                  big:
//                 [1]                                [1]
//         [2]            [3]                    [2]       [4]
//      [4]    [ ]     [5]     [ ]            [3]   [ ]
//                  [ ]   [6]

breadthBT(t) == List(1, 2, 3)			 // true
breadthBT(tt) == List(1, 2, 3, 4, 5, 6)  // true (DFS would give List(1, 2, 4, 3, 5, 6)
breadthBT(tree_empty) == List()			 // true
breadthBT(tree_root) == List(5)			 // true
breadthBT(tree_zero) == List(4, -2, -2)  // true
breadthBT(tree_big) == List(1, 2, 4, 3)  // true (DFS would give List(1, 2, 3, 4))
