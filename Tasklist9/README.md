<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #9</strong></a>
    ·
    <a href="./tasklist9.pdf">TaskList9.pdf</a>
  </p>
</p>

---

# **Task #1**

## Analyse the program: why the counter value is not as it could be expected to be?

```scala
object Task1a extends App {
  var counter = 0                        // counter variable

  def readWriteCounter(): Unit = {
    val incrementedCounter = counter + 1 // reading counter
    counter = incrementedCounter         // writing to counter
    // counter += 1                      // shorter code
  }

  def startTest(): Unit = {
    val p, q = new Thread(() => for(_ <- 0 until 200000) readWriteCounter())
    counter = 0
    val startTime = System.nanoTime
    p.start(); q.start()
    p.join(); q.join()
    val estimatedTime = (System.nanoTime - startTime)/1000000
    println(s"The value of counter = $counter")
    println(s"Estimated time = ${estimatedTime}ms, Available processors = ${Runtime.getRuntime.availableProcessors}")
  }

  startTest()
}
```

### a)

```scala
// a)
//    Q: Jak wyjaśnisz różne wartości licznika?
//    A: Przedewszystkim:
//       Zakładając, że jest jeden procesor - "jednocześnie" wykonują się dwa procesy, których akcje się przeplatają.
//       Po upływie pewnego odcinka czasu następuje wywłaszczenie, co może się zdarzyć, gdy akurat nastąpił odczyt
//       zmiennej, ale nie jej zapis, przez co drugi procesor czyta jeszcze nie zaktualizowaną zmienną przez drugi proc.
//
//       Druga obserwacja:
//       Praktycznie zawsze (wyjątek zdarzył się tylko raz), pierwsze odpalanie funkcji podbije licznik (znacząco)
//       najwięcej razy w porównaniu do każdej następnej iteracji wykonywania pomiaru. Dzieje się to przez to, że
//       pierwsze wykonanie funkcji jest "najświeższe", a w tle kończą się / sprzątają po sobie inne procesy.
//
//       Wpływa to też na czas wykonania funkcji (~65ms, w porównaniu do następnych 2ms), a przez wydłużony czas
//       wykonywania, spowolnione wątki "nie zderzają" się, aż tak często podczas wyścigu, co odzwierciedlone
//       jest w liczbie końcowej (często ~37k / 40k; następne 20k +- 12k / 40k).
//
//    Q: Wskaż fragment kodu, który jest źródłem problemów.
//    A: Oczywiście problemem jest sytuacja, kiedy dwa wątki równocześnie odczytują wartość zmiennej.
//       W tej sytuacji oba wątki podbiją wartość o jeden, a następnie ją zapiszą, co gubi jedną inkrementację.
//       Jest to tzw. "wyścig danych" (ang. data race)
```

### b) Fix it with synchronised code

```scala
object Task1b extends App {
  var counter = 0

  def readWriteCounter(): Unit = this.synchronized {
    val incrementedCounter = counter + 1
    counter = incrementedCounter
  }

  def startTest(): Unit = {
    val p, q = new Thread(() => for(_ <- 0 until 200000) readWriteCounter())
    counter = 0
    val startTime = System.nanoTime
    p.start(); q.start()
    p.join(); q.join()
    val estimatedTime = (System.nanoTime - startTime)/1000000
    println(s"The value of counter = $counter")
    println(s"Estimated time = ${estimatedTime}ms, Available processors = ${Runtime.getRuntime.availableProcessors}")
  }

  startTest()
}
```

### c) Fix it using Semaphores.

```scala
object Task1c extends App {
  val semaphore = new Semaphore(1)
  var counter = 0

  def readWriteCounter(): Unit = this.synchronized {
    semaphore.acquire()
    val incrementedCounter = counter + 1
    counter = incrementedCounter
    semaphore.release()
  }

  def startTest(): Unit = {
    val p, q = new Thread(() => for(_ <- 0 until 200000) readWriteCounter())
    counter = 0
    val startTime = System.nanoTime
    p.start(); q.start()
    p.join(); q.join()
    val estimatedTime = (System.nanoTime - startTime)/1000000
    println(s"The value of counter = $counter")
    println(s"Estimated time = ${estimatedTime}ms, Available processors = ${Runtime.getRuntime.availableProcessors}")
  }

  startTest()
}
```

# **Task #2**

## Implement method `parallel` that takes two blocks of arguments as parameter and executes them simultaneously.

```scala
object Task2 extends App {
  var counter = 0

  // Notice: Skipping check for n <= 1 -> false.
  def isLargePrime(n: Int): Boolean = !((2 until n-1) exists (n % _ == 0))

  def parallel[A, B](blockA: => A, blockB: => B): (A, B) = {
    var aOut: Option[A] = Option.empty
    var bOut: Option[B] = Option.empty
    val q = new Thread(() => aOut = Some(blockA))
    val p = new Thread(() => bOut = Some(blockB))
    p.start(); q.start()
    p.join(); q.join()
    (aOut.get, bOut.get)
  }

  def readWriteCounter(): Unit = {
    val incrementedCounter = counter + 1
    counter = incrementedCounter
  }

  def startTest[A](block: => A): Unit = {
    val startTime = System.nanoTime
    block
    val estimatedTime = (System.nanoTime - startTime)/1000000
    println(s"Estimated time = ${estimatedTime}ms, Available processors = ${Runtime.getRuntime.availableProcessors}")
  }

}
```

### _Tests_

```scala
println("Core tests:")
println(parallel("a"+1, "b"+2))
println(parallel(Thread.currentThread.getName, Thread.currentThread.getName))

println("Checking two primes sequentially:")
startTest(println(isLargePrime(30000001)))
startTest(println(isLargePrime(30000023)))

println("\nChecking two primes in parallel:")
startTest(println(parallel(isLargePrime(30000001), isLargePrime(30000023))))
println("\nBumping counter in parallel:")
startTest(parallel(for(_ <- 0 until 200000) readWriteCounter(), for(_ <- 0 until 200000) readWriteCounter()))
println(s"Race Test: $counter / 400000")
```

# **Task #3**

## Implement method `periodically` that takes the number of repetitions and pause-timer as arguments. Method should make use of daemons.

```scala
object Task3 extends App {
  def periodically(duration: Long, times: Int)(block: => Unit): Unit = {

    def repeater(times: Int): Unit = if (times > 0) { Thread.sleep(duration); block; repeater(times - 1) }

    val thread = new Thread(() => repeater(times))
    thread.setDaemon(true)
    thread.start()
  }

  periodically(1000, 5){print("y ")}
  periodically(1000, 25){print("x ")}
  Thread.sleep(10000)
  println("Done sleeping")
}
```

### _Tests_

```scala
periodically(1000, 5){print("y ")}
periodically(1000, 25){print("x ")}

Thread.sleep(10000)
println("Done sleeping")
// output: x y x y x y x y x y x x x x Done sleeping
```
