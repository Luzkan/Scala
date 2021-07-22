<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #8</strong></a>
    ·
    <a href="./tasklist8.pdf">TaskList8.pdf</a>
  </p>
</p>

---

# **Task #1 a)**

## Write the class extending the queue as a circular array (all operations are performed by modulo the size of the array)

```scala
class QueueMut[E: ClassTag](val size: Int = 1000) extends MyQueue[E] {
  private val queue: Array[E] = new Array[E](size)
  private var front: Int = -1
    private var rear: Int = -1

  override def enqueue(x: E): Unit =
    if (this.isFull)       { throw new FullException("Full Queue") }
    else if (this.isEmpty) { front = 0; rear = 0; queue(rear) = x }
    else                   { rear = (rear + 1) % size; queue(rear) = x }

  override def dequeue(): Unit =
    if (this.isEmpty)       { throw new NoSuchElementException("Empty Queue") }
    else if (front == rear) { front -= 1; rear -= 1; }
    else                    { front = (front + 1) % size}

  override def first: E =
    if (this.isEmpty) throw new NoSuchElementException("Pusta kolejka")
    else queue(front)

  override def isEmpty: Boolean =
    front == -1

  override def isFull: Boolean =
    (rear + 1) % size == front
}


object QueueMut {
  def apply[E: ClassTag](xs: E*): QueueMut[E] =
    xs.foldLeft(new QueueMut[E](xs.size))((acc: QueueMut[E], elem: E) => {
      acc.enqueue(elem)
      acc
    })

  def empty[E: ClassTag](capacity: Int = 1000): QueueMut[E] = new QueueMut[E](capacity)
}
```

# **Task #1 b)**

## It's basically the same task, but I did a different implementation as requested on labs.

```scala
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
```
