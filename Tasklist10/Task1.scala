
// 1. Na wykładzie 9 (str. 42-43) był przedstawiony program producent/konsument z ograniczonym buforem cyklicznym.
//
//		a) Przepisz ten program, wykorzystując zamiast klasy BoundedBuffer klasę biblioteczną
//       java.util.concurrent.ArrayBlockingQueue.
//
//		b) W programie z podpunktu a) utwórz kilka producentów i konsumentów. Nadaj im
//			 unikatowe nazwy, np. Producer1, Consumer1 itd. W jednym z testów utwórz dwa
//	 		 producenty i trzy konsumenty. Dlaczego program się nie kończy?
//
//		c) Z programu w podpunkcie b) usuń definicje klas Producer i Consumer. Wykorzystaj
//			 ExecutionContext do wykonywania odpowiadających im zadań. W jednym z testów
//		     utwórz dwa producenty i trzy konsumenty. Dlaczego program się kończy?

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// -------------------------------------------------------------------------
// a)

import java.util.concurrent.ArrayBlockingQueue
import scala.concurrent.ExecutionContext

class Producer(name: String, buf: ArrayBlockingQueue[String]) extends Thread(name) {
  val nameId = s"${name.charAt(0)}${name.charAt(name.length() - 1)}"
  override def run(): Unit = {
    for (i <- 1 to 10)  { buf.put(s"m$i"); println(s"[$nameId]: $buf") }
    buf.put("Done")
    println(s"[$nameId]: $buf")
  }
}

class Consumer(name: String, buf: ArrayBlockingQueue[String]) extends Thread(name) {
  val nameId = s"${name.charAt(0)}${name.charAt(name.length() - 1)}"
  override def run(): Unit = {
    var msg = ""
    do { msg = buf.take; println(s"[$nameId]: $msg") } while (msg != "Done")
  }
}

object Task1a {
  def main(args: Array[String]): Unit = {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    new Producer("Producer0", buf).start()
    new Consumer("Consumer0", buf).start()
  }
}

// -------------------------------------------------------------------------
// b)

// Program will not end because one of the consumers will awaiting for singal, which won't ever be emitted :(
// This is so called thread-life error: the program is in a state from which it cannot go any further.

object Task1b {
  def main(args: Array[String]) {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    for (i <- 0 to 1) new Producer(s"Producer$i", buf).start()
    for (i <- 0 to 2) new Consumer(s"Consumer$i", buf).start()
  }
}

// -------------------------------------------------------------------------
// c)

// We use ExecutionContext, which is implmenetation of Executor. Executor created daemon thread and worker threads,
// and terminating execution of the daemon thread terminates execution of all worker threads.
// The program ends because the main thread: (Thread.sleep(1500)) has a clearly set end time - 0.5s.

object Task1c {
  def main(args: Array[String]) {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    for (i <- 0 to 1) ExecutionContext.global.execute(() => { new Producer(s"Producer$i", buf).start() })
    for (i <- 0 to 2) ExecutionContext.global.execute(() => { new Consumer(s"Consumer$i", buf).start() })
    Thread.sleep(500)
  }
}
