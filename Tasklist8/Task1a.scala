import scala.reflect.ClassTag

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Dana jest następująca klasa abstrakcyjna dla generycznych kolejek modyfikowalnych.
//
	  	class FullException(msg: String) extends Exception(msg)

		abstract class MyQueue[E] {
			@throws[FullException]
			def enqueue(x: E): Unit
			def dequeue(): Unit
			@throws[NoSuchElementException]
			def first: E
			def isEmpty: Boolean
			def isFull: Boolean
		}
//
//		a) Napisz klasę generyczną QueueMut, rozszerzającą powyższą klasę abstrakcyjną,
//			 w której kolejka jest implementowana przez tablicę cykliczną (wszystkie operacje na
//			 indeksach tablicy cyklicznej są wykonywane modulo rozmiar tablicy). Przeprowadź
//			 testy na małej kolejce, którą całkowicie zapełnisz.
//
//		b) Zdefiniuj obiekt towarzyszący z metodami:
// 			 def apply[E: ClassTag](xs: E*): QueueMut[E] = ???
// 			 def empty[E: ClassTag](capacity: Int = 1000) : QueueMut[E] = ???
//
//		Wszystkie definicje oraz proste testy w obiekcie singletonowym z metodą main umieść w pliku Lista8.scala.
//
//		Uwaga. Tablice w języku Java nie mogą być generyczne (wykład 7, str. 36). W Scali jest to
//			   jednak możliwe, ale w czasie tworzenia tablicy generycznej potrzebna jest informacja
//			   o wytartym typie elementów tej tablicy. Można to zrobić, wykorzystując znacznik typu (ang.
//			   class tag), który sam jest typu scala.reflect.ClassTag, co jest zilustrowane poniżej:
//		       import reflect.ClassTag
//		       class QueueMut[E: ClassTag](val capacity: Int = 1000) extends MyQueue[E]

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

class QueueMut[E: ClassTag](val capacity: Int = 1000) extends MyQueue[E] {
	private val queue: Array[E] = new Array[E](capacity)
	private var front: Int = -1
    private var rear: Int = -1

	override def enqueue(x: E): Unit =
		if (this.isFull) 	   { throw new FullException("Full Queue") }
		else if (this.isEmpty) { front = 0; rear = 0; queue(rear) = x }
		else 				   { rear = (rear + 1) % capacity; queue(rear) = x }

	override def dequeue(): Unit =
		if (this.isEmpty) 		{ throw new NoSuchElementException("Empty Queue") }
		else if (front == rear) { front -= 1; rear -= 1; }
		else                    { front = (front + 1) % capacity}

	override def first: E =
		if (this.isEmpty) throw new NoSuchElementException("Pusta kolejka")
		else queue(front)

	override def isEmpty: Boolean =
		front == -1

	override def isFull: Boolean =
		(rear + 1) % capacity == front
}


object QueueMut {
	def apply[E: ClassTag](xs: E*): QueueMut[E] =
		xs.foldLeft(new QueueMut[E](xs.capacity))((acc: QueueMut[E], elem: E) => {
			acc.enqueue(elem)
			acc
		})

	def empty[E: ClassTag](capacity: Int = 1000): QueueMut[E] = new QueueMut[E](capacity)
}

object Lista8 {
	def main(args: Array[String]): Unit = {
		val q_2 = new QueueMut[Int](2)
		val q_1 = new QueueMut[Double](1)
		val q_0 = new QueueMut[String](0) // This situation leads to ArithmaticException: / by zero

		println("\nInitialization Test")
		println(s"[Q2] IsEmpty: (is: ${q_2.isEmpty}, should be: true)")
		println(s"[Q1] IsEmpty: (is: ${q_1.isEmpty}, should be: true)")
		println(s"[Q2] IsFull: (is: ${q_2.isFull}, should be: false)")
		println(s"[Q1] IsFull: (is: ${q_1.isFull}, should be: false)")

		println("\nAdding one item to both queues")
		q_2.enqueue(1)
		q_1.enqueue(1.0)
		println(s"[Q2] IsEmpty: (is: ${q_2.isEmpty}, should be: false)")
		println(s"[Q1] IsEmpty: (is: ${q_1.isEmpty}, should be: false)")

		println(s"[Q2] First:   (is: ${q_2.first}, should be: 1)")
		println(s"[Q1] First:   (is: ${q_1.first}, should be: 1.0)")

		println(s"[Q2] IsFull:  (is: ${q_2.isFull}, should be: false)")
		println(s"[Q1] IsFull:  (is: ${q_1.isFull}, should be: true)")

		println("\nTesting illegal actions")

		def enqueueWhenFull: Boolean =
			try { q_1.enqueue(2); false }
			catch { case _: FullException => true }
		println(s"[Q1] Enqueue When Full:     (thrown error: ${enqueueWhenFull}, should be: true)")

		println(s"[Q1] Dequeue When Item is Present:  (is: ${q_1.dequeue()}, should be: ())")

		def dequeueWhenEmpty: Boolean =
			try { q_1.dequeue(); false }
			catch { case _: NoSuchElementException => true }
		println(s"[Q1] Dequeue When IsEmpty:  (thrown error: ${dequeueWhenEmpty}, should be: true)")

		def firstWhenEmpty: Boolean =
			try { q_1.first; false }
			catch { case _: NoSuchElementException => true }
		println(s"[Q1] First When IsEmpty:    (thrown error: ${firstWhenEmpty}, should be: true)")

		println("\nWork Test")

		def sampleUsageTest: Boolean = {
			val q_5 = new QueueMut[Int](5)
			q_5.enqueue(14)
			q_5.enqueue(22)
			q_5.enqueue(13)
			q_5.enqueue(-6)
			q_5.dequeue()
			q_5.dequeue()
			q_5.enqueue(9)
			q_5.enqueue(20)
			q_5.enqueue(5)
			q_5.first == 13
		}
		println(s"[Q5] Sample Usage Test: (should be: ${sampleUsageTest}, should be: true)")

		println("\nConstructor Test")

		println("new QueueMut", (new QueueMut()).isEmpty)
		println("QueueMut()", QueueMut().isEmpty)
		println("QueueMut.empty()", QueueMut.empty().isEmpty)
		println("QueueMut.empty(Int)", QueueMut.empty(100).isEmpty)
		println("QueueMut('a', 'b', 'c')", QueueMut('a', 'b', 'c').isFull)
	}
}

Lista8.main(Array())
