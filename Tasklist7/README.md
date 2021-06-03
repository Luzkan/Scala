<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #7</strong></a>
    ·
    <a href="./tasklist7.pdf">TaskList7.pdf</a>
  </p>
</p>

---

# **Task #1**

## Define a general class for a covariant unmodifiable queue represented by two lists.

```scala
class UnderflowException(msg: String) extends Exception(msg)

class MyQueue[+T] private (private val in: List[T], private val out: List[T]) {
	def this() = this(Nil, Nil)

	// Enqueue to the queue by prepending to the out list
	def enqueue[E >: T] (new_element: E): MyQueue[E] =
		in match {
			// Each time we create a new MyQueue because (obviously) it is immutable
			case _ :: _  => new MyQueue[E](in, new_element :: out)
			case   Nil   => new MyQueue[E](new_element :: in, out)
		}

	// Dequeue from the queue by taking the first element of the in list
	def dequeue: MyQueue[T] =
		in match {
			// if the in list is empty:
			// 		- reverse the `out` list and replace the `in` list with it
			// 	  - replace the `out` list with an empty list
			case _ :: Nil  => new MyQueue[T](out.reverse, Nil)
			case _ :: tail => new MyQueue[T](tail, out)
			case Nil => this
		}

	// Adding an element to the front is O(1)
	// Reverse operation is O(n) but that is amortised over all dequeues to ~O(1)
	// Thereby, this is ~O(1) immutable queue implementation.

	def first: T =
		in match {
			case head :: _ => head
			case Nil => throw new UnderflowException("Empty Queue.")
		}

	def isEmpty: Boolean = in == Nil

	override def toString: String = {s"($in, $out)"}
}

object MyQueue {
	// asterisk in (xs: A*) simply allows for multiple arguments
	def apply[T](xs: T*): MyQueue[T] = new MyQueue[T](xs.toList, Nil)
	def empty[T]: MyQueue[T] = new MyQueue[T](Nil, Nil)
}
```

### _Tests_

```scala
// Initializations
new MyQueue      // res0: MyQueue[Nothing] = (List(), List())
MyQueue()		 // res1: MyQueue[Nothing] = (List(), List())
MyQueue(1)		 // res2: MyQueue[Int] = (List(1), List())
MyQueue(1, 2)	 // res3: MyQueue[Int] = (List(1, 2), List())
MyQueue.empty	 // res4: MyQueue[Nothing] = (List(), List())

// Enqueue
val q_0 = MyQueue()
// > (List(), List())
val q_1 = MyQueue().enqueue(1)
// > (List(1), List())
val q_2 = MyQueue().enqueue(1).enqueue(2)
// > (List(1), List(2))
val q_3 = MyQueue().enqueue(1).enqueue(2).enqueue(3)
// > (List(1), List(3, 2))
val q_4 = MyQueue().enqueue(1).enqueue(2).enqueue(3).enqueue(4)
// > (List(1), List(4, 3, 2))

// Dequeue
q_2.dequeue	                 // (List(2), List())
q_2.dequeue.dequeue          // (List(), List())
q_2.dequeue.dequeue.dequeue	 // (List(), List())
q_3.dequeue                  // (List(2, 3), List())
q_3.dequeue.dequeue          // (List(3), List())
q_3.dequeue.dequeue.dequeue	 // (List(), List())

// IsEmpty
q_3.isEmpty                          // false
q_3.dequeue.isEmpty                  // false
q_3.dequeue.dequeue.isEmpty          // false
q_3.dequeue.dequeue.dequeue.isEmpty  // true

// Various
q_4.isEmpty
// > false
q_4.dequeue
// > (List(2, 3, 4), List())
q_4.dequeue.enqueue(5)
// > (List(2, 3, 4), List(5))
q_4.dequeue.enqueue(5).dequeue
// > (List(3, 4), List(5))
q_4.dequeue.enqueue(5).dequeue.enqueue(6)
// > (List(3, 4), List(6, 5))
q_4.dequeue.enqueue(5).dequeue.enqueue(6).first
// > 3
try { q_0.first } catch { case exception: UnderflowException => print(exception) }
// > UnderflowException: Empty Queue
```

# **Task #2**

## Write a `breadthBT [A] function: BT [A] => List [A]` traversing binary tree in breadth and returning a list of values stored in tree nodes.

```scala
def breadthBT[A](tree: BT[A]): List[A] = {
	def search(visited: List[A]) (queue: MyQueue[BT[A]]): List[A] = {
		queue.first match {
			case Node(e, l, r) => e :: search(e :: visited)(queue.dequeue.enqueue(l).enqueue(r))
			case Empty => if (!queue.dequeue.isEmpty) search(visited)(queue.dequeue) else Nil
		}
	}
	search (Nil) (MyQueue(tree))
}
```

### _Tests_

```scala
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)
val tt = Node(1 Node(2, Node(4, Empty, Empty), Empty), Node(3, Node(5, Empty, Node(6, Empty, Empty)), Empty))
val tree_empty = Empty
val tree_root = Node(5, Empty, Empty)
val tree_zero = Node(4, Node(-2, Empty, Empty), Node(-2, Empty, Empty))
val tree_big = Node(1, Node(2, Node(3, Empty, Empty), Empty), Node(4, Empty, Empty))


breadthBT(t) == List(1, 2, 3)			 // true
breadthBT(tt) == List(1, 2, 3, 4, 5, 6)  // true (DFS would give List(1, 2, 4, 3, 5, 6)
breadthBT(tree_empty) == List()			 // true
breadthBT(tree_root) == List(5)			 // true
breadthBT(tree_zero) == List(4, -2, -2)  // true
breadthBT(tree_big) == List(1, 2, 4, 3)  // true (DFS would give List(1, 2, 3, 4))
```
