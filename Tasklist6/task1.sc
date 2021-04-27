import scala.annotation.tailrec
// 1. Jedna z pętli w języku Scala ma następującą składnię: while (warunek) wyrażenie, np.
//    var count = 0
//    while (count < 5) {
//        println(count)
//        count += 1
//    }
//    Napisz funkcję whileLoop (bez używania efektów obliczeniowych), która pobiera dwa
//    argumenty: warunek oraz wyrażenie i dokładnie symuluje działanie pętli while
//    (również składniowo). Jakiego typu (i dlaczego) muszą być argumenty i wynik
//    funkcji?

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ----------------------------
// -      My Extra Notes      -
// ----------------------------

// This was super-helpfull:
//    https://docs.scala-lang.org/tour/by-name-parameters.html

// -------------------------------------------------------------------------

@tailrec
def whileLoop(condition: => Boolean)(body: => Unit): Unit =
  if (condition) { body; whileLoop(condition)(body) }

var count = 0
whileLoop(count < 5) { println(count); count+= 1 }
