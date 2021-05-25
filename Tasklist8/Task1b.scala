import java.util.NoSuchElementException
import scala.reflect.ClassTag

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Dana jest następująca klasa abstrakcyjna dla generycznych kolejek modyfikowalnych.

class FullException(msg: String) extends Exception(msg)
abstract class MyQueue[E] {
  @throws[FullException]
  def enqueue(x: E): Unit
  def dequeue: Unit
  @throws[NoSuchElementException]
  def first: E
  def isEmpty: Boolean
  def isFull: Boolean
}

//    a) Napisz klasę generyczną QueueMut, rozszerzającą powyższą klasę abstrakcyjną,
//       w której kolejka jest implementowana przez tablicę cykliczną (wszystkie operacje na
//	     indeksach tablicy cyklicznej są wykonywane modulo rozmiar tablicy). Implementacja
//       ma być zgodna z poniższym rysunkiem, czyli rozmiar tablicy musi być o 1 większy od
//       pojemności kolejki (dzięki temu indeksy f oraz r wystarczą do sprawdzenia, czy
//       kolejka jest pusta czy pełna). Metoda dequeue dla pustej kolejki ma pozostawiać
//       pustą kolejkę.
//
//    b) Zdefiniuj obiekt towarzyszący z metodami:
//       def apply[E: ClassTag](xs: E*): QueueMut[E] = ???
//       def empty[E: ClassTag](capacity: Int = 1000) : QueueMut[E] = ???
//
//    Przeprowadź testy na małej kolejce, którą całkowicie zapełnisz. Przetestuj przejście przez
//    „sklejenie” tablicy. Wszystkie definicje oraz proste testy w obiekcie singletonowym Lista8
//    z metodą main umieść w pliku Lista8.scala.

//    Uwaga. Tablice w języku Java nie mogą być generyczne (wykład 7, str. 36). W Scali jest to
//	         jednak możliwe, ale w czasie tworzenia tablicy generycznej potrzebna jest informacja
//           o wytartym typie elementów tej tablicy. Można to zrobić, wykorzystując znacznik typu (ang.
//           class tag), który sam jest typu scala.reflect.ClassTag (wykład 7, str. 38), co jest zilustrowane
//           poniżej:
//           import reflect.ClassTag
//           class QueueMut[E: ClassTag]private(val capacity: Int = 1000) extends MyQueue[E]


// Idx:    0          1        2        3
// Empty: [( f|r   ), (   ),   (  ),    (  )]
//        [( f|[a] ), ( r ),   (  ),    (  )]
//        [( f|[a] ), ( [b] ), ( r ),   (  )]
// Full:  [( f|[a] ), ( [b] ), ( [c] ), ( r )]

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

class QueueMut[E: ClassTag] private (val capacity: Int = 1000) extends MyQueue[E] {
  private val queue_size: Int = capacity+1
  private val queue: Array[E] = new Array[E](queue_size)
  private var front: Int = 0
  private var rear: Int = 0
  private val Null: E = Null

  @throws[FullException]
  override def enqueue(x: E): Unit = {
    if (this.isFull) throw new FullException("Full Queue")
    else {queue(rear) = x; rear = (rear + 1) % queue_size}
  }

  override def dequeue: Unit =
    if (this.isEmpty) throw new NoSuchElementException("Empty Queue")
    else {queue(front) = Null; front = (front + 1) % queue_size}

  @throws[NoSuchElementException]
  override def first: E =
    if (this.isEmpty) throw new NoSuchElementException("Empty Queue")
    else queue(front)

  override def isEmpty: Boolean = front == rear
  override def isFull: Boolean = (rear + 1) % queue_size == front
}

object QueueMut {
  def empty[E: ClassTag](capacity: Int = 1000) : QueueMut[E] = new QueueMut[E](capacity)
  def apply[E: ClassTag](xs: E*): QueueMut[E] =
    xs.foldLeft(new QueueMut[E](xs.size))((que: QueueMut[E], element: E) => { que.enqueue(element); que })
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------


object Lista8 {
  def main(args: Array[String]): Unit = {
    val q_2 = new QueueMut[Int](2)
    val q_1 = new QueueMut[Double](1)
    println("\nInitializations:")
    println(s"[Q2] QueueMut[Int](capacity = 2)")
    println(s"[Q1] QueueMut[Double](capacity = 1)\n")
    println(s"[Q2] IsEmpty: (is: ${q_2.isEmpty}, should be: true)")
    println(s"[Q1] IsEmpty: (is: ${q_1.isEmpty}, should be: true)")
    println(s"[Q2] IsFull:  (is: ${q_2.isFull},  should be: false)")
    println(s"[Q1] IsFull:  (is: ${q_1.isFull},  should be: false)")

    println("\nAdding one item to both Queues")
    q_2.enqueue(1)
    q_1.enqueue(1.0)

    println(s"[Q2] IsEmpty: (is: ${q_2.isEmpty}, should be: false)")
    println(s"[Q1] IsEmpty: (is: ${q_1.isEmpty}, should be: false)")
    println(s"[Q2] IsFull:  (is: ${q_2.isFull}, should be: false)")
    println(s"[Q1] IsFull:  (is: ${q_1.isFull},  should be: true)")
    println(s"[Q2] First:   (is: ${q_2.first},   should be: 1)")
    println(s"[Q1] First:   (is: ${q_1.first}, should be: 1.0)")

    println("\nAdding one more item to both Queues")
    q_2.enqueue(2)
    def enqueueWhenFull: Boolean =
      try { q_1.enqueue(2.0); false }
      catch { case _: FullException => true }

    println(s"[Q2] IsFull:  (is: ${q_2.isFull}, should be: true)")
    println(s"[Q1] Enqueue When Full:  (thrown error: ${enqueueWhenFull}, should be: true)")
    println(s"[Q2] First:   (is: ${q_2.first},   should be: 1)")
    println(s"[Q1] First:   (is: ${q_1.first}, should be: 1.0)")

    println("\nRemoving an item from both Queues")
    q_2.dequeue
    println(s"[Q1] Dequeue When Item is Present:  (is: ${q_1.dequeue}, should be: ())")
    println(s"[Q2] IsEmpty: (is: ${q_2.isEmpty}, should be: false)")
    println(s"[Q1] IsEmpty: (is: ${q_1.isEmpty},  should be: true)")
    println(s"[Q2] IsFull:  (is: ${q_2.isFull}, should be: false)")
    println(s"[Q1] IsFull:  (is: ${q_1.isFull}, should be: false)")
    println(s"[Q2] First:   (is: ${q_2.first},     should be: 2)")

    def firstWhenEmpty: Boolean =
      try { q_1.first; false }
      catch { case _: NoSuchElementException => true }
    println(s"[Q1] First When IsEmpty:    (thrown error: ${firstWhenEmpty}, should be: true)")

    def dequeueWhenEmpty: Boolean =
      try { q_1.dequeue; false }
      catch { case _: NoSuchElementException => true }
    println(s"[Q1] Dequeue When IsEmpty:  (thrown error: ${dequeueWhenEmpty}, should be: true)")

    println("\nWork Test")
    def sampleUsageTest: Boolean = {
      val q_5 = new QueueMut[Int](5)
			println(s"[Q5] QueueMut[Int](capacity = 5)")
      q_5.enqueue(14)
      q_5.enqueue(22)
      q_5.enqueue(13)
      q_5.enqueue(-6)
      q_5.dequeue
      q_5.dequeue
      q_5.enqueue(9)
      q_5.enqueue(20)
      q_5.enqueue(5)
      q_5.first == 13
    }
    println(s"[Q5] Sample Usage Test: (evaluated properly: ${sampleUsageTest}, should be: true)")

    println("\nSample Queue Traversing on List Input")
    println(s"[Q4] QueueMut(1, 2, 3, 4)")
    val q_4 = QueueMut(1, 2, 3, 4)
    println(s"[Q4] First: (is: ${q_4.first}, should be: 1)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 2)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 3)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 4)")
    q_4.enqueue(5)
    q_4.enqueue(6)
    q_4.enqueue(7)
    println(s"[Q4] First: (is: ${q_4.first}, should be: 4)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 5)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 6)")
    q_4.dequeue
    println(s"[Q4] First: (is: ${q_4.first}, should be: 7)")
    q_4.dequeue
    def dequeueWhenEmptyQ4: Boolean =
      try { q_4.dequeue; false }
      catch { case _: NoSuchElementException => true }
    def firstWhenEmptyQ4: Boolean =
      try { q_4.first; false }
      catch { case _: NoSuchElementException => true }
    println(s"[Q4] Dequeue When IsEmpty:  (thrown error: ${dequeueWhenEmptyQ4}, should be: true)")
    println(s"[Q4] First When IsEmpty:    (thrown error: ${firstWhenEmptyQ4}, should be: true)")
  }
}
