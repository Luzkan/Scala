<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #13</strong></a>
    ·
    <a href="./tasklist13.pdf">TaskList13.pdf</a>
  </p>
</p>

---

# **Task #1**

## Define **two** `observable` objects, one of which emits an `event` every `5` and the other one emits an `event` every `12` second but both of them do not emit anything when time is multiple of `30` seconds. One of them should use the `merge` method.

```scala
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
```

# **Task #2**

## Define **one** `observable` object that emits an `event` every `5` and `12` second but not when time is multiple of `30` seconds. Use the `filter` method.

```scala
case class Or[A](p1: A => Boolean, p2: A => Boolean) extends (A => Boolean) {
  def apply(a: A): Boolean = p1(a) || p2(a)
}

object Task1b extends App {
  val mod5mod12 = Observable.interval(1.seconds).filter(
    OnlyIfAAndNotB(Or(_ % 5 == 0, _ % 12 == 0), _ % 30 == 0)).map(n => s"$n")
  mod5mod12.subscribe(log, e => log(s"Unexpected error: $e"), () => log("No more modulo (% 5) or (% 12)."))
  Thread.sleep(41000)
}
```
