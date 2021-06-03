<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #11</strong></a>
    ·
    <a href="./tasklist11.pdf">TaskList11.pdf</a>
    ·
    <a href="./data/">Testing Data for Task #3</a>
  </p>
</p>

---

# **Task #1**

## Define function `pairFut`

Define a function `pairFut[A, B] (fut1: Future[A], fut2: Future[B]): Future[(A, B)] = ???`

## **a) Using `zip` method**

```scala
object Task1a {
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
```

## **b) Using `for`**

```scala
object Task1b {
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
}
```

# **Task #2**

## Add `exists` method to `Future[T]` type

Add the `exists` method to the Future [T] type:

`def exists (p: T => Boolean): Future [Boolean] = ???`

The resulting `Future` object is set to be true if and only if the original object computes successfully and the predicate `p` returns `true`, otherwise the resulting `Future` object is `false`. Use the _implicit_ class and _implicit conversion_ mechanism.

Perform three tests: when the predicate is satisfied, when the predicate is not satisfied and when an exception is thrown.

## **a) Using `promise`**

```scala
object Task2a {
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
```

## **b) Without `promise`**

```scala
object Task2b {
  implicit class FutureOps[T](val self: Future[T]) {
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
```

# **Task #3**

## Count the amount of words in all files in a given directory

Count the number of words in each text file of a specified folder and print the result as tuple `(filename, number of words)`, sorted in ascending order _(by the number of words)_. We can assume for the sake of simplicity that words are separated by spaces (use the split method). Calculations should be performed asynchronously. The program has to be written functionally.

```scala
object Task3 {
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
```
