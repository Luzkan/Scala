// 1. Zdefiniuj klasę generyczną dla kowariantnej kolejki niemodyfikowalnej, reprezentowanej
//		przez dwie listy.
//
//  	W ten sposób reprezentowane są kolejki niemodyfikowalne w językach czysto
//  	funkcyjnych, a także w Scali (patrz dokumentacja).
//
//  	Wskazówka. Wzoruj się na klasie dla stosu z wykładu 7 (str. 8 i 27) oraz dokumentacji
//  	scala.collection.immutable.Queue (zaimplementuj tylko metody z powyższej specyfikacji).
//    Zdefiniuj obiekt towarzyszący z metodami apply i empty.
//
//		Utworzenie nowej kolejki ma być możliwe na cztery sposoby:
//			- new MyQueue
//			- MyQueue()
//			- MyQueue.empty
//			- MyQueue(‘a’, ‘b’, ‘c’)
//
//		Para list:							([x1; x2; ...; xm], [y1; y2; ...; yn])
//		Reprezentuje kolejkę:		  x1 x2 ... xm yn ... y2 y1.
//		Pierwsza lista reprezentuje początek kolejki, a druga – koniec kolejki.
//
//		Elementy w drugiej liście są zapamiętane w odwrotnej kolejności, żeby
//		wstawianie było wykonywane w czasie stałym (na początek listy).
//
//  	enqueue(y, q) modyfikuje kolejkę następująco:
//  	(xl, [y1; y2; ...; yn])  (xl, [y; y1; y2; ...; yn]).
//
//  	Elementy w pierwszej liście są pamiętane we właściwej kolejności, co umożliwia
//  	szybkie usuwanie pierwszego elementu. dequeue(q) modyfikuje kolejkę następująco:
//  	([x1; x2; ...; xm], yl)  ([x2; ...; xm], yl).
//
//		Kiedy pierwsza lista zostaje opróżniona, druga lista jest odwracana i wstawiana w miejsce pierwszej:
//  	([], [y1; y2; ...; yn])  ([yn; ... y2; y1], []).
//
//  	Reprezentacja kolejki jest w postaci normalnej, jeśli nie wygląda tak: ([], [y1; y2; ...; yn]) dla n1.
//
//  	Wszystkie operacje kolejki mają zwracać reprezentację w postaci normalnej, dzięki czemu
//  	pobieranie wartości pierwszego elementu nie spowoduje odwracania listy.
//
//  	Odwracanie drugiej listy po opróżnieniu pierwszej też może się wydawać kosztowne.
//  	Jeśli jednak oszacujemy nie koszt pesymistyczny (oddzielnie dla każdej operacji kolejki), ale
//  	koszt zamortyzowany (uśredniony dla całego czasu istnienia kolejki), to okaże się, że
//  	zamortyzowany koszt operacji wstawiania i usuwania z kolejki jest stały.

// Update #1:
//	 - "new MyQueue" is now supported

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ----------------------------
// -      My Extra Notes      -
// ----------------------------

// This is Stack implementation as given on the lectures.

class UnderflowException(msg: String) extends Exception(msg)

// Main Constructor is private! konstruktor główny jest prywatny!
class MyStack[T] private (private val rep: List[T]) {
	def push(x: T) = new MyStack(x::rep)
	def in: T = rep match {
		case x::_ => x
		case Nil => throw new UnderflowException("Empty stack")
	}
	def pop: MyStack[T] = rep match {
		case _::xs => new MyStack(xs)
		case Nil => this
	}
	def isEmpty = rep == Nil
}

// Companion Object
object MyStack {
	def apply[T](xs: T*) = new MyStack[T](xs.toList.reverse)
	def empty[T] = new MyStack[T](Nil)
}


// -------------------------------------------------------------------------

// in    -> FIFO (First-In, First-Out)
// out   -> LIFO (Last In,  First Out)

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
			// 	  - reverse the `out` list and replace the `in` list with it
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

// --------
// - Tests

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
q_2.dequeue					 // (List(2), List())
q_2.dequeue.dequeue			 // (List(), List())
q_2.dequeue.dequeue.dequeue	 // (List(), List())
q_3.dequeue					 // (List(2, 3), List())
q_3.dequeue.dequeue 		 // (List(3), List())
q_3.dequeue.dequeue.dequeue	 // (List(), List())

// IsEmpty
q_3.isEmpty							 // false
q_3.dequeue.isEmpty					 // false
q_3.dequeue.dequeue.isEmpty			 // false
q_3.dequeue.dequeue.dequeue.isEmpty	 // true

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