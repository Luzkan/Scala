package com.example

import com.example.Utils.log
import rx.lang.scala._
import scala.concurrent.duration._

// Lista #13: Marcel Jerzyk (indeks: 244979)

object Utils {
  def log(msg: String): Unit = println(s"${Thread.currentThread.getName}: $msg")
}

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Zdefiniuj obiekt Observable, emitujący zdarzenie co 5 sekund i co 12 sekund, ale nie
//    wtedy, kiedy czas jest wielokrotnością 30 sekund. Emitowane zdarzenia to:
//    5, 10, 12, 15, 20, 24, 25, 35, 36, 40 itd.
//
//    a) Zdefiniuj dwie obserwable. Jedna emituje zdarzenie co 5 sekund, a druga co 12 sekund
//       i utwórz z nich jedną za pomocą metody merge.
//       Subskrybent ma wyświetlać zdarzenia w poniższym formacie (nazwy wątków należy
//       dodać przy wyświetlaniu):
//
//       RxComputationScheduler-1: 5
//       RxComputationScheduler-1: 10
//       RxComputationScheduler-2: 12
//       RxComputationScheduler-1: 15
//       RxComputationScheduler-1: 20
//       RxComputationScheduler-2: 24
//       RxComputationScheduler-1: 25
//       RxComputationScheduler-1: 35
//       RxComputationScheduler-2: 36
//       RxComputationScheduler-1: 40
//
//    b) Zrób to samo, definiując tylko jedną obserwablę (wykorzystaj metodę filter).
//       Oto początek przykładowego wydruku. Zauważ, że teraz wykorzystywany jest tylko
//       jeden wątek.
//
//       RxComputationScheduler-1: 5
//       RxComputationScheduler-1: 10
//       RxComputationScheduler-1: 12
//       RxComputationScheduler-1: 15
//       RxComputationScheduler-1: 20
//       RxComputationScheduler-1: 24
//       RxComputationScheduler-1: 25
//       RxComputationScheduler-1: 35
//       RxComputationScheduler-1: 36
//       RxComputationScheduler-1: 40

// ----------------------------------------------------------------------------
// a)

case class OnlyIfAAndNotB[A](p1: A => Boolean, p2: A => Boolean) extends (A => Boolean) {
  def apply(a: A): Boolean = p1(a) && !p2(a)
}

object Task1a extends App {
  val a = Observable.interval(1.seconds).filter(OnlyIfAAndNotB(_ % 5 == 0, _ % 30 == 0)).map(n => s"[Mod 5]: $n")
  val b = Observable.interval(1.seconds).filter(OnlyIfAAndNotB(_ % 12 == 0, _ % 30 == 0)).map(n => s"[Mod 12]: $n")
  val c = a.merge(b)
  c.subscribe(p => log(p), e => log(s"Unexpected error: $e"), () => log("No more modulo (% 5) or (% 12)."))
  Thread.sleep(41000)
}

// ----------------------------------------------------------------------------
// b)

case class Or[A](p1: A => Boolean, p2: A => Boolean) extends (A => Boolean) {
  def apply(a: A): Boolean = p1(a) || p2(a)
}

object Task1b extends App {
  val mod5mod12 = Observable.interval(1.seconds).filter(
    OnlyIfAAndNotB(Or(_ % 5 == 0, _ % 12 == 0), _ % 30 == 0)).map(n => s"$n")
  mod5mod12.subscribe(log, e => log(s"Unexpected error: $e"), () => log("No more modulo (% 5) or (% 12)."))
  Thread.sleep(41000)
}
