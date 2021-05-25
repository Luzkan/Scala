package Lista9

import java.util.concurrent.Semaphore

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Przeanalizuj poniższy program. Dwa wątki zwiększają 200 000 razy wspólny licznik o 1.
//    Po ich zakończeniu wartość licznika powinna oczywiście wynosić 400 000. Uruchom ten program kilka razy.

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

//    a) Jak wyjaśnisz różne wartości licznika? W wyjaśnieniu załóż, że mamy tylko jeden procesor,
//       więc nie ma akcji jednoczesnych. Wskaż fragment kodu, który jest źródłem problemów.
//       Odpowiedź należy umieścić w postaci komentarza na początku pliku Lista9.scala.
//
//    b) Popraw powyższy program, wykorzystując mechanizm kodu synchronizowanego (blokada wewnętrzna, monitory).
//
//    c) Popraw powyższy program, wykorzystując mechanizm semaforów (klasa java.util.concurrent.Semaphore).

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// -------------------------------------------------------------------------
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

// -------------------------------------------------------------------------
// b)

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

// -------------------------------------------------------------------------
// c)

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

// ----------------------------------------------------------------------------
// ------------------------------    Task2    ---------------------------------
// ----------------------------------------------------------------------------

// 2. Zaimplementuj metodę parallel, która jako argumenty bierze dwa bloki kodu, wykonuje je
//    jednocześnie w osobnych wątkach i zwraca wyniki ich obliczeń w postaci pary.
//    def parallel[A, B](block1: =>A, block2: =>B): (A, B) = ???
//    Przykładowe testy:
//      - println(parallel("a"+1, "b"+2))
//      - println(parallel(Thread.currentThread.getName, Thread.currentThread.getName))

// ----------------------------
// -      My Extra Notes      -
// ----------------------------

// https://codereview.stackexchange.com/questions/203972/run-two-computations-in-parallel-and-return-the-results-together
// https://stackoverflow.com/questions/37849130/parallel-processing-pattern-in-scala

// -------------------------------------------------------------------------

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
}


// ----------------------------------------------------------------------------
// ------------------------------    Task3    ---------------------------------
// ----------------------------------------------------------------------------

// 3. Zaimplementuj metodę periodically, która jako argumenty bierze interwał czasowy duration
//    (w milisekundach), maksymalną liczbę powtórzeń times oraz blok kodu block. Metoda
//    tworzy wątek demona, który wykonuje podany blok kodu z pauzami trwającymi duration
//    milisekund, maksymalnie times razy.
//
//    def periodically(duration: Long, times: Int)(block: => Unit): Unit = ???
//
//    Test (na końcu metody main) może wyglądać tak:
//      $ periodically(1000, 5){print("y ")}
//      $ periodically(1000, 25){print("x ")}
//      $ Thread.sleep(10000)
//      $ println("Done sleeping")
//    Uruchom ten program kilka razy. Wynik powinien być taki (z dokładnością do przeplotu):
//      > y x y x x y x y x y x x x x x Done sleeping
//    Dlaczego “x” nie zostało wyświetlone 25 razy? (Odpowiedź umieść w komentarzu.)

// ----------------------------
// -      My Extra Notes      -
// ----------------------------

// Q: Dlaczego “x” nie zostało wyświetlone 25 razy?
// A: Wątek-demon (ang. daemon thread) jest wątkiem, którego jedynym zadaniem jest służenie innym wątkom.
//    W przeciwieństwie do wątków użytkownika, wątki demony nie stanowią przeszkody w zakończeniu działania programu.
//    Jeśli aktywne są tylko wątki-demony, to proces kończy działanie.

// -------------------------------------------------------------------------

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
