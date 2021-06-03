import java.io.IOException
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.io.Source
import java.io.File

// Lista #11: Marcel Jerzyk (indeks: 244979)

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Zdefiniuj funkcję
//    def pairFut[A, B] (fut1: Future[A], fut2: Future[B]): Future[(A, B)] = ???
//
//    a) Wykorzystaj metodę zip (wykład 11, str. 18)
//
//    b) Wykorzystaj for (wykład 11, str. 19)

// -------------------------------------------------------------------------
// a)

object Zad1a {
  def pairFut[A, B](fut1: Future[A], fut2: Future[B]): Future[(A, B)] = fut1 zip fut2

  def main(args: Array[String]): Unit = {
    val f1 = Future { 1 }
    val f2 = Future { "A" }
    val future: Future[(Int, String)] = pairFut(f1, f2)
    future onComplete {
      case Success(value) => println(s"Future completed successfully with $value.")
      case Failure(error) => println(s"Future completed with error: ${error.printStackTrace()}.")
    }
    Thread.sleep(2500)
  }
}

// -------------------------------------------------------------------------
// b)

object Zad1b {
  def pairFut[A, B] (fut1: Future[A], fut2: Future[B]): Future[(A, B)] = for {a <- fut1; b <- fut2} yield (a, b)

  def main(args: Array[String]): Unit = {
    val f1 = Future { 2 }
    val f2 = Future { "B" }
    val future: Future[(Int, String)] = pairFut(f1, f2)
    future onComplete {
      case Success(value) => println(s"Future completed successfully with $value.")
      case Failure(error) => println(s"Future completed with error: ${error.printStackTrace()}.")
    }
    Thread.sleep(2500)
  }
}

// ----------------------------------------------------------------------------
// ------------------------------    Task2    ---------------------------------
// ----------------------------------------------------------------------------

// 2. Do typu Future[T] dodaj metodę exists:
//
//   def exists(p: T => Boolean): Future[Boolean] = ???
//
//   Wynikowy obiekt Future ma zawierać wartość true wtedy i tylko wtedy, obliczenia obiektu
//   oryginalnego kończą się pomyślnie i predykat p zwraca wartość true, w przeciwnym razie
//   wynikowy obiekt Future ma zawierać wartość false. Wykorzystaj klasę implicytną
//   i mechanizm niejawnych konwersji (wykład 11, str. 22).
//
//   a) Wykorzystaj promesę
//
//   b) Nie korzystaj z promesy (użyj map)
//
//   Przeprowadź trzy testy: kiedy predykat jest spełniony, kiedy predykat nie jest spełniony,
//   kiedy rzucany jest wyjątek.

// ----------------------------------------------------------------------------
// a)

object Zad2a {
  implicit class FutureOps[T](val self: Future[T]) {
    def exists(p: T => Boolean): Future[Boolean] = {
      val p2 = Promise[Boolean]()
      self onComplete {
        case Success(value) => p2 success p(value)
        case Failure(_)     => p2 success false
      }
      p2.future
    }
  }

  def main(args: Array[String]): Unit = {
    val f1 = Future {Thread.sleep(2750); 1 }
    val f2 = Future {Thread.sleep(2500); 0 }
    val f3 = Future {Thread.sleep(2250); "A" }
    val f4 = Future {Thread.sleep(2000); "" }
    val f5 = Future[String] { throw new Exception() }
    println(s"[f1] 1 > 0: ${Await.result(f1.exists(x => x > 0), 3.second)} (expected: true)")
    println(s"[f2] 0 > 0: ${Await.result(f2.exists(x => x > 0), 3.second)} (expected: false)")
    println(s"[f3] 'A'.nonEmpty: ${Await.result(f3.exists(x => x.nonEmpty), 3.second)} (expected: true)")
    println(s"[f4] ''.nonEmpty: ${Await.result(f4.exists(x => x.nonEmpty), 3.second)} (expected: false)")
    println(s"[f5] throw new Exception().nonEmpty: ${Await.result(f5.exists(x => x.nonEmpty), 1.second)} (expected: false)")
  }
}


// ----------------------------------------------------------------------------
// b)

object Zad2b {
  implicit class FutureOps[T](val self: Future[T]) {
    // https://www.scala-lang.org/api/2.13.3/scala/concurrent/Future.html#Transformations
    def exists(p: T => Boolean): Future[Boolean] = self map p recover {case _: Throwable => false }
  }

  def main(args: Array[String]): Unit = {
    val f1 = Future {Thread.sleep(2750); 1 }
    val f2 = Future {Thread.sleep(2500); 0 }
    val f3 = Future {Thread.sleep(2250); "A" }
    val f4 = Future {Thread.sleep(2000); "" }
    val f5 = Future[String] { throw new Exception() }
    println(s"[f1] 1 > 0: ${Await.result(f1.exists(x => x > 0), 3.second)} (expected: true)")
    println(s"[f2] 0 > 0: ${Await.result(f2.exists(x => x > 0), 3.second)} (expected: false)")
    println(s"[f3] 'A'.nonEmpty: ${Await.result(f3.exists(x => x.nonEmpty), 3.second)} (expected: true)")
    println(s"[f4] ''.nonEmpty: ${Await.result(f4.exists(x => x.nonEmpty), 3.second)} (expected: false)")
    println(s"[f5] throw new Exception().nonEmpty: ${Await.result(f5.exists(x => x.nonEmpty), 1.second)} (expected: false)")
  }
}

// ----------------------------------------------------------------------------
// ------------------------------    Task3    ---------------------------------
// ----------------------------------------------------------------------------

// 3. Należy policzyć liczbę słów w każdym pliku tekstowym zadanego folderu i wydrukować
//    wynik w postaci par (nazwa pliku, liczba słów), posortowany niemalejąco względem liczby
//    słów. Możemy założyć dla uproszczenia, że słowa są oddzielone spacjami (wykorzystaj
//    metodę split). Obliczenia należy przeprowadzać asynchronicznie. Program ma być napisany
//    funkcyjnie. Poniżej jest jego schemat

// ----------------------------------------------------------------------------

object Zad3 {


  def countWordsInFilesInDirectory(path: String): Future[Seq[(String, Int)]] = getFileList(path) flatMap processFiles

  // Log function from example programs package attached to Lecture #11.
  private def log(msg: String): Unit = println(s"${Thread.currentThread.getName}: $msg")

  // Returns Future containing list of files in a given directory (instead of list of file _names_ [strings])
  private def getFileList(docRoot: String): Future[Seq[File]] = Future { new File(docRoot).listFiles.filter(_.isFile).toList }

  // Calls getWordCount() on all files, each of which returns a Future, then convert Seq[Futures] into single Future
  private def processFiles(files: Seq[File]): Future[Seq[(String, Int)]] = { Future.sequence(files.map(getWordCount)) }

  private def getWordCount(file: File): Future[(String, Int)] =
    Future {
      val f = Source.fromFile(file)
      try (file.getName, f.getLines.foldLeft(0)((acc, line) => acc + line.split(" +").count(_.nonEmpty)))
      finally f.close()
    } recover {
      case e: IOException => println(s"In/Out Error: ${e.getMessage}")
      (file.getName, -1)
    }

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val future = countWordsInFilesInDirectory(path)  // "./data/"
    val promise = Promise[Seq[(String, Int)]]
    promise.completeWith(future)
    log(s"Status: ${future.isCompleted}")

    promise.future onComplete {
      case Success(result) =>
        log("Future completed successfully. Results:")
        result.sortWith((l, r) => l._2 < r._2) foreach println
      case Failure(error) => log(s"Future completed with error:\n${error.printStackTrace()}")
    }
    Thread.sleep(5000)
  }
}
